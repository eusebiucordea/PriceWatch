package com.pricewatch.pricewatch.ejb;

import com.microsoft.playwright.*;
import com.pricewatch.pricewatch.entities.PriceHistory;
import com.pricewatch.pricewatch.entities.ProductLink;
import com.pricewatch.pricewatch.entities.Products;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.ejb.Schedule;

@Stateless
public class ScraperBean {

    private static final Logger LOG = Logger.getLogger(ScraperBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    // injectam serviciul de email creat anterior
    @Inject
    private EmailBean emailBean;

    private static class StoreConfig {
        String searchUrlFormat;
        String firstResultSelector;

        public StoreConfig(String searchUrlFormat, String firstResultSelector) {
            this.searchUrlFormat = searchUrlFormat;
            this.firstResultSelector = firstResultSelector;
        }
    }

//    private static final Map<String, StoreConfig> STORES = Map.of(
//            "emag", new StoreConfig("https://www.emag.ro/search/%s", "a.card-v2-title"),
//            "altex", new StoreConfig("https://altex.ro/cauta/?q=%s", "a.Product-link"),
//            "pc garage", new StoreConfig("https://www.pcgarage.ro/cautare/%s", "div.product_box_name a"),
//            "flanco", new StoreConfig("https://www.flanco.ro/catalogsearch/result/?q=%s", "a.product-item-link"),
//            "mediagalaxy", new StoreConfig("https://mediagalaxy.ro/cauta/?q=%s", "a.Product-link")
//    );
//
//    private static final Map<String, String> STORE_SELECTORS = Map.of(
//            "emag", "p.product-new-price",
//            "altex", ".text-red-brand .Price-int",
//            "pc garage", ".price_num",
//            "flanco", ".price",
//            "mediagalaxy", ".text-red-brand .Price-int"
//    );

    // declaram hartile ca fiind goale, dar gata sa primească date hashmap
    private static final Map<String, StoreConfig> STORES = new HashMap<>();
    private static final Map<String, String> STORE_SELECTORS = new HashMap<>();

    static {
        // emag
        STORES.put("emag", new StoreConfig("https://www.emag.ro/search/%s", "a.card-v2-title"));
        STORE_SELECTORS.put("emag", "p.product-new-price");

        // aletx media galaxy
        STORES.put("altex", new StoreConfig("https://altex.ro/cauta/?q=%s", "a[href*='/cpd/']"));
        STORE_SELECTORS.put("altex", "div.text-red-brand span.Price-int");

        STORES.put("mediagalaxy", new StoreConfig("https://mediagalaxy.ro/cauta/?q=%s", "a[href*='/cpd/']"));
        STORE_SELECTORS.put("mediagalaxy", "div.text-red-brand span.Price-int");

        // pcgarage
        STORES.put("pc garage", new StoreConfig("https://www.pcgarage.ro/cautare/%s", "div.pb-name a, div.product_box_name a"));
        STORE_SELECTORS.put("pc garage", "span.price_num");

        // flanco
        STORES.put("flanco", new StoreConfig("https://www.flanco.ro/catalogsearch/result/?q=%s", "a.product-item-link"));
        STORE_SELECTORS.put("flanco", "span.special-price span.price, span.price-wrapper span.price");
    }

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

    @Schedule(hour = "*", minute = "*/15", persistent = false)
    // hour = "*" pentru verificare la fiecare ora fixa
    // persistent = false - nu recupereaza verificarile perdute
    // "*/5" verifica daca se imaprte la 5 spre exemplu, minute = "*/5" verifica daca 12:11 se imparte la 5, nu se imparte doar cand este 12:15
    public void scrapeAllProducts() {
        LOG.info("Puls de verificare: cautam link-uri programate pentru acest moment...");

        // tragem doar link-urile unde 'nextCheckAt' a fost depasit
        List<ProductLink> dueLinks = em.createQuery(
                        "SELECT pl FROM ProductLink pl WHERE pl.nextCheckAt IS NULL OR pl.nextCheckAt <= :acum",
                        ProductLink.class)
                .setParameter("acum", LocalDateTime.now())
                .getResultList();

        if (dueLinks.isEmpty()) {
            LOG.info("Products dont need verification at the moment");
            return;
        }

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            try (BrowserContext context = browser.newContext(contextOptions)) {

                for (ProductLink link : dueLinks) {
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

                            // SETARE INTERVAL URMATOR
                            int interval = (link.getCheckIntervalMinutes() != null) ? link.getCheckIntervalMinutes() : 720;
                            link.setNextCheckAt(LocalDateTime.now().plusMinutes(interval));

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
                                    // pastram pretul vechi inainte de a l actualiza pentru a putea face comparatia
                                    Double oldPrice = product.getCurrent_price();

                                    // verificam daca pretul actual este mai mic decat cel pe care il stiam
                                    if (oldPrice != null && lowestCurrentPrice < oldPrice) {

                                        // calculam cat la suta reprezinta reducerea
                                        double dropPercentage = ((oldPrice - lowestCurrentPrice) / oldPrice) * 100.0;

                                        // extragem din baza de date adresa de email si procentul din alerta setata de utilizatori
                                        List<Object[]> usersToNotify = em.createQuery(
                                                        "SELECT u.email, w.targetDiscount FROM Users u JOIN WatchList w ON u.id = w.userId " +
                                                                "WHERE w.productId = :productId AND w.targetDiscount IS NOT NULL", Object[].class)
                                                .setParameter("productId", product.getId())
                                                .getResultList();

                                        for (Object[] row : usersToNotify) {
                                            String userEmail = (String) row[0];
                                            Integer targetDiscount = (Integer) row[1];

                                            // daca reducerea acopera dorinta utilizatorului trimitem alerta
                                            if (dropPercentage >= targetDiscount) {
                                                emailBean.sendPriceAlertEmail(userEmail, product.getName(), oldPrice, lowestCurrentPrice);
                                            }
                                        }
                                    }

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

                        // daca da eroare magazinul il reprogramam sa incerce din nou peste 1 ora,
                        // ca sa nu ne blocam la infinit pe el incercand la fiecare 15 minute
                        link.setNextCheckAt(LocalDateTime.now().plusMinutes(60));
                        em.merge(link);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "eroare initializare playwright", e);
        }
    }

    public void searchAndAddNewProduct(String productName) {
        LOG.info("Incepere cautare automata pentru: " + productName);

        // cream produsul de baza cu numele temporar
        Products newProduct = new Products();
        newProduct.setName(productName);
        newProduct.setImage_url("");
        newProduct.setCurrent_price(999999.99);
        newProduct.setAll_time_low(999999.99);
        em.persist(newProduct);
        em.flush();

        String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
        Double lowestFoundPrice = 999999.99;
        String firstFoundImage = "";
        boolean isNameUpdated = false; // luam h1 o singura data

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
                        // cautam produsul
                        page.navigate(searchUrl);
                        page.waitForSelector(config.firstResultSelector, new Page.WaitForSelectorOptions().setTimeout(15000));
                        String extractedUrl = page.locator(config.firstResultSelector).first().getAttribute("href");

                        if (extractedUrl != null && !extractedUrl.isEmpty()) {
                            if (extractedUrl.startsWith("/")) {
                                if (storeName.equals("altex")) extractedUrl = "https://altex.ro" + extractedUrl;
                                else if (storeName.equals("mediagalaxy")) extractedUrl = "https://mediagalaxy.ro" + extractedUrl;
                            }

                            // intram pe link ul gasit pentru a lua pretul si detaliile
                            String priceSelector = STORE_SELECTORS.get(storeName);
                            if (priceSelector != null) {
                                page.navigate(extractedUrl);
                                page.waitForSelector(priceSelector, new Page.WaitForSelectorOptions().setTimeout(15000));

                                String priceText = page.locator(priceSelector).first().innerText();
                                Double price = parsePriceText(priceText);

                                if (price != null) {
                                    // extragem h1 de pe pagina produsului (daca nu l-am extras deja)
                                    if (!isNameUpdated) {
                                        try {
                                            if (page.locator("h1").count() > 0) {
                                                String h1Name = page.locator("h1").first().innerText().trim();
                                                newProduct.setName(h1Name);
                                                isNameUpdated = true;
                                            }
                                        } catch (Exception e) {
                                            LOG.warning("Nu am putut extrage H1 de pe " + storeName);
                                        }
                                    }

                                    // salvam imaginea
                                    if (firstFoundImage.isEmpty()) {
                                        try {
                                            Locator imgLocator = page.locator("meta[property='og:image']");
                                            if (imgLocator.count() > 0) {
                                                firstFoundImage = imgLocator.first().getAttribute("content");
                                            }
                                        } catch (Exception e) {
                                            LOG.warning("Nu am putut extrage imaginea de pe " + storeName);
                                        }
                                    }

                                    // determinam cel mai mic pret
                                    if (price < lowestFoundPrice) {
                                        lowestFoundPrice = price;
                                    }

                                    // salvam link ul magazinului
                                    ProductLink link = new ProductLink();
                                    link.setProduct(newProduct);
                                    link.setStoreName(storeName);
                                    link.setUrl(extractedUrl);
                                    link.setLastPrice(price);
                                    link.setLastChecked(LocalDateTime.now());
                                    link.setCheckIntervalMinutes(720);
                                    link.setNextCheckAt(LocalDateTime.now().plusMinutes(720));
                                    em.persist(link);

                                    // salvam istoricul
                                    PriceHistory history = new PriceHistory();
                                    history.setProductLink(link);
                                    history.setPrice(price);
                                    history.setRecordedAt(LocalDateTime.now());
                                    em.persist(history);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.warning("Eroare pe " + storeName + " pt produs: " + productName + ". Motiv: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Eroare initializare Playwright in cautare automata", e);
        }

        // update in baza de date
        if (lowestFoundPrice < 999999.99) {
            newProduct.setCurrent_price(lowestFoundPrice);
            newProduct.setAll_time_low(lowestFoundPrice);
            if (!firstFoundImage.isEmpty()) {
                newProduct.setImage_url(firstFoundImage);
            }
            em.merge(newProduct);
        }
    }

    // functia care calculeaza cat de mult se aseamana doua nume (returneaza intre 0.0 si 1.0)
    private double calculateSimilarity(String name1, String name2) {
        if (name1 == null || name2 == null) return 0.0;

        // curatam caracterele speciale si facem litere mici
        String clean1 = name1.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String clean2 = name2.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");

        // impartim propozitia intr-o lista de cuvinte unice
        java.util.Set<String> words1 = new java.util.HashSet<>(java.util.Arrays.asList(clean1.split("\\s+")));
        java.util.Set<String> words2 = new java.util.HashSet<>(java.util.Arrays.asList(clean2.split("\\s+")));

        // eliminam cuvintele care nu ne ajuta sa identificam unicitatea produsului
        java.util.Set<String> stopWords = new java.util.HashSet<>(java.util.Arrays.asList(
                "telefon", "mobil", "smartphone", "smart", "tv", "televizor", "laptop", "gaming", "dual", "sim", "casti", "smartwatch",
                "wireless", "tableta", "tastatura"
        ));
        words1.removeAll(stopWords);
        words2.removeAll(stopWords);

        // eliminam spatiile goale ramase accidental
        words1.remove("");
        words2.remove("");

        if (words1.isEmpty() || words2.isEmpty()) return 0.0;

        // vedem cate cuvinte au in comun
        java.util.Set<String> intersection = new java.util.HashSet<>(words1);
        intersection.retainAll(words2);

        // calculam procentajul (ne raportam la titlul mai scurt pentru a nu fi penalizati de magazinele care scriu romane in titlu)
        int minSize = Math.min(words1.size(), words2.size());
        if (minSize == 0) return 0.0;

        return (double) intersection.size() / minSize;
    }

    // functia care cauta in toata baza de date produsul cel mai asemanator
    private Products findBestMatchingProduct(String newProductName) {
        List<Products> allProducts = em.createQuery("SELECT p FROM Products p", Products.class).getResultList();

        Products bestMatch = null;
        double highestScore = 0.0;

        for (Products product : allProducts) {
            double currentScore = calculateSimilarity(newProductName, product.getName());

            // daca scorul e mai mare decat ce am gasit pana acum il tinem minte
            if (currentScore > highestScore) {
                highestScore = currentScore;
                bestMatch = product;
            }
        }

        // setam pragul de acceptare la 95% daca se potrivesc cel putin 95% e acelasi produs
        if (highestScore >= 0.90) {
            LOG.info("Am gasit o potrivire! '" + newProductName + "' se aseamana in proportie de " + (highestScore * 100) + "% cu '" + bestMatch.getName() + "'");
            return bestMatch;
        }
        return null;
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

                Products existingProduct = findBestMatchingProduct(productName);

                Products targetProduct;

                // daca l-am gasit (scor peste 80%) il folosim daca nu cream unul nou
                if (existingProduct != null) {
                    LOG.info("produsul exista deja in baza de date (potrivire gasita). adaugam doar link-ul.");
                    targetProduct = existingProduct;

                    // actualizam pretul cel mai mic la nivel de produs daca este cazul
                    if (price < targetProduct.getAll_time_low()) {
                        targetProduct.setAll_time_low(price);
                        targetProduct.setCurrent_price(price);
                        em.merge(targetProduct);
                    }
                } else {
                    LOG.info("produs nou (nu am gasit nicio potrivire). il cream acum.");
                    targetProduct = new Products();
                    targetProduct.setName(productName);
                    targetProduct.setImage_url(imageUrl);
                    targetProduct.setCurrent_price(price);
                    targetProduct.setAll_time_low(price);
                    em.persist(targetProduct);
                    em.flush();
                }

                // salvam link-ul magazinului si il legam de produs (vechi sau nou)
                ProductLink link = new ProductLink();
                link.setProduct(targetProduct);
                link.setStoreName(storeName);
                link.setUrl(url);
                link.setLastPrice(price);
                link.setLastChecked(LocalDateTime.now());
                link.setCheckIntervalMinutes(720);
                link.setNextCheckAt(LocalDateTime.now().plusMinutes(720));
                em.persist(link);
                em.flush();

                // salvam istoricul de pret pentru acest link
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

    //  pentru a lasa adminul sa schimbe intervalul din interfata
    public void updateProductCheckInterval(Long productId, int minutes) {
        if (minutes < 15) minutes = 15; // siguranta

        List<ProductLink> links = em.createQuery("SELECT pl FROM ProductLink pl WHERE pl.product.id = :prodId", ProductLink.class)
                .setParameter("prodId", productId)
                .getResultList();

        for(ProductLink link : links) {
            link.setCheckIntervalMinutes(minutes);

            if (link.getLastChecked() != null) {
                link.setNextCheckAt(link.getLastChecked().plusMinutes(minutes));
            } else {
                link.setNextCheckAt(LocalDateTime.now().plusMinutes(minutes));
            }
            em.merge(link);
        }
    }
}