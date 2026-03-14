package com.pricewatch.pricewatch.ejb;

import com.microsoft.playwright.*;
import com.pricewatch.pricewatch.entities.PriceHistory;
import com.pricewatch.pricewatch.entities.ProductLink;
import com.pricewatch.pricewatch.entities.Products;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Stateless
public class ScraperBean {

    private static final Logger LOG = Logger.getLogger(ScraperBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    private static class StoreConfig {
        String searchUrlFormat;
        String firstResultSelector;

        public StoreConfig(String searchUrlFormat, String firstResultSelector) {
            this.searchUrlFormat = searchUrlFormat;
            this.firstResultSelector = firstResultSelector;
        }
    }

    private static final Map<String, StoreConfig> STORES = Map.of(
            "emag", new StoreConfig("https://www.emag.ro/search/%s", "a.card-v2-title"),
            "altex", new StoreConfig("https://altex.ro/cauta/?q=%s", "a.Product-link"),
            "pc garage", new StoreConfig("https://www.pcgarage.ro/cautare/%s", "div.product_box_name a"),
            "flanco", new StoreConfig("https://www.flanco.ro/catalogsearch/result/?q=%s", "a.product-item-link"),
            "mediagalaxy", new StoreConfig("https://mediagalaxy.ro/cauta/?q=%s", "a.Product-link")
    );

    private static final Map<String, String> STORE_SELECTORS = Map.of(
            "emag", "p.product-new-price",
            "altex", ".text-red-brand .Price-int",
            "pc garage", ".price_num",
            "flanco", ".price",
            "mediagalaxy", ".text-red-brand .Price-int"
//            "evomag", ".pret_rons",
//            "itgalaxy", ".price-value",
//            "vexio", ".price-value",
//            "cel", ".product-price",
//            "domo", ".product-price"
    );

    private Double parsePriceText(String priceText) {
        try {
            String cleanText = priceText.replaceAll("[^0-9.,]", "").trim();
            cleanText = cleanText.replace(".", "").replace(",", ".");
            return Double.parseDouble(cleanText);
        } catch (Exception e) {
            LOG.warning("eroare parsare pret: " + priceText);
            return null;
        }
    }

    private Double getLowestCurrentPriceForProduct(Long productId) {
        try {
            return em.createQuery("SELECT MIN(pl.lastPrice) FROM ProductLink pl WHERE pl.product.id = :prodId", Double.class)
                    .setParameter("prodId", productId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void scrapeAllProducts() {
        LOG.info("start verificare preturi globale");

        List<ProductLink> allLinks = em.createQuery("SELECT pl FROM ProductLink pl", ProductLink.class).getResultList();

        if (allLinks.isEmpty()) {
            return;
        }

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            try (BrowserContext context = browser.newContext(contextOptions)) {

                for (ProductLink link : allLinks) {
                    try (Page page = context.newPage()) {
                        page.navigate(link.getUrl());

                        String storeName = link.getStoreName();
                        if (storeName == null) continue;

                        String priceSelector = STORE_SELECTORS.get(storeName.toLowerCase().trim());
                        if (priceSelector == null) continue;

                        page.waitForSelector(priceSelector, new Page.WaitForSelectorOptions().setTimeout(10000));
                        String priceText = page.locator(priceSelector).first().innerText();

                        Double noulPret = parsePriceText(priceText);

                        if (noulPret != null) {
                            // 1. actualizam ProductLink (magazinul curent)
                            link.setLastPrice(noulPret);
                            link.setLastChecked(LocalDateTime.now());
                            em.merge(link);

                            // 2. inregistram istoricul
                            PriceHistory history = new PriceHistory();
                            history.setProductLink(link);
                            history.setPrice(noulPret);
                            history.setRecordedAt(LocalDateTime.now());
                            em.persist(history);

                            // 3. ne ocupam de produsul principal
                            Products product = link.getProduct();
                            if (product != null) {
                                // facem flush pentru ca baza de date sa inregistreze noul lastPrice la linkul de mai sus
                                // inainte de a calcula minimul absolut
                                em.flush();

                                // 4. recalculam care e cel mai mic pret dintre toate magazinele ACUM
                                Double lowestCurrentPrice = getLowestCurrentPriceForProduct(product.getId());

                                if (lowestCurrentPrice != null) {
                                    // actualizam pretul de astazi
                                    product.setCurrent_price(lowestCurrentPrice);

                                    // 5. verificam daca acest nou pret curent minim este all time low
                                    Double curentAllTimeLow = product.getAll_time_low();

                                    if (curentAllTimeLow == null || lowestCurrentPrice < curentAllTimeLow) {
                                        product.setAll_time_low(lowestCurrentPrice);
                                    }
                                    // salvam produsul
                                    em.merge(product);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.warning("eroare verificare link: " + link.getUrl());
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "eroare initializare playwright", e);
        }
    }

    public void searchAndAddNewProduct(String productName) {
        Products newProduct = new Products();
        newProduct.setName(productName);
        newProduct.setImage_url("");
        newProduct.setAll_time_low(Double.MAX_VALUE);
        em.persist(newProduct);
        em.flush();

        String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            try (BrowserContext context = browser.newContext(contextOptions)) {

                for (Map.Entry<String, StoreConfig> entry : STORES.entrySet()) {
                    String storeName = entry.getKey();
                    StoreConfig config = entry.getValue();
                    String searchUrl = String.format(config.searchUrlFormat, encodedProductName);

                    try (Page page = context.newPage()) {
                        page.navigate(searchUrl);
                        page.waitForSelector(config.firstResultSelector, new Page.WaitForSelectorOptions().setTimeout(10000));
                        String extractedUrl = page.locator(config.firstResultSelector).first().getAttribute("href");

                        if (extractedUrl != null && !extractedUrl.isEmpty()) {
                            if (extractedUrl.startsWith("/")) {
                                if (storeName.equals("altex")) extractedUrl = "https://altex.ro" + extractedUrl;
                            }

                            ProductLink link = new ProductLink();
                            link.setProduct(newProduct);
                            link.setStoreName(storeName);
                            link.setUrl(extractedUrl);
                            em.persist(link);
                        }
                    } catch (Exception e) {
                        LOG.warning("eroare cautare " + storeName + " pt produs: " + productName);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "eroare cautare automata playwright", e);
        }
    }

    public void addProductFromUrl(String url, String storeName) {
        storeName = storeName.toLowerCase().trim();
        String priceSelector = STORE_SELECTORS.get(storeName);

        if (priceSelector == null) return;

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            try (BrowserContext context = browser.newContext(contextOptions);
                 Page page = context.newPage()) {

                page.navigate(url);
                page.waitForSelector(priceSelector, new Page.WaitForSelectorOptions().setTimeout(15000));

                String productName = "Produs Necunoscut";
                try {
                    if (page.locator("h1").count() > 0) {
                        productName = page.locator("h1").first().innerText().trim();
                    } else {
                        productName = page.title().trim();
                    }
                } catch (Exception e) {
                    LOG.warning("nume produs indisponibil");
                }

                String imageUrl = "";
                try {
                    Locator imgLocator = page.locator("meta[property='og:image']");
                    if (imgLocator.count() > 0) {
                        imageUrl = imgLocator.first().getAttribute("content");
                    }
                } catch (Exception e) {
                    LOG.warning("imagine produs indisponibila");
                }

                String priceText = page.locator(priceSelector).first().innerText();
                Double price = parsePriceText(priceText);

                if (price == null) return;

                Products newProduct = new Products();
                newProduct.setName(productName);
                newProduct.setImage_url(imageUrl);
                newProduct.setCurrent_price(price);
                newProduct.setAll_time_low(price);
                em.persist(newProduct);
                em.flush();

                ProductLink link = new ProductLink();
                link.setProduct(newProduct);
                link.setStoreName(storeName);
                link.setUrl(url);
                link.setLastPrice(price);
                link.setLastChecked(LocalDateTime.now());
                em.persist(link);
                em.flush();

                PriceHistory history = new PriceHistory();
                history.setProductLink(link);
                history.setPrice(price);
                history.setRecordedAt(LocalDateTime.now());
                em.persist(history);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "eroare salvare produs din url", e);
        }
    }
}