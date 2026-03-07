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

@Stateless
public class ScraperBean {

    private static final Logger LOG = Logger.getLogger(ScraperBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    // am pus numele magazinelor cu litere mici pentru a evita problemele de scriere
    private static final Map<String, String> STORE_SELECTORS = Map.of(
            "emag", "p.product-new-price",
            "altex", ".Price-int",
            "pc garage", "div.pb-price"
    );

    private Double parsePriceText(String priceText) {
        try {
            String cleanText = priceText.replaceAll("[^0-9.,]", "").trim();
            cleanText = cleanText.replace(".", "").replace(",", ".");
            return Double.parseDouble(cleanText);
        } catch (Exception e) {
            LOG.warning("Eroare la parsarea pretului: " + priceText);
            return null;
        }
    }

    public void scrapeAllProducts() {
        LOG.info("Verificarea preturilor pentru toate produsele...");

        List<ProductLink> allLinks = em.createQuery("SELECT pl FROM ProductLink pl", ProductLink.class).getResultList();

        if (allLinks.isEmpty()) {
            LOG.warning("Nu am gasit niciun link in baza de date.");
            return;
        }

        LOG.info("Am gasit " + allLinks.size() + " link-uri de verificat.");

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {

            // cream un context nou de browser si ii setam un User-Agent fals pentru a pacali protectiile antibot (Altex)
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            try (BrowserContext context = browser.newContext(contextOptions)) {

                for (ProductLink link : allLinks) {
                    LOG.info("Navigam catre: " + link.getUrl());

                    try (Page page = context.newPage()) {
                        page.navigate(link.getUrl());

                        String storeName = link.getStoreName();

                        if (storeName == null) {
                            LOG.warning("Numele magazinului este NULL in baza de date pentru linkul: " + link.getUrl());
                            continue;
                        }

                        // transformam numele magazinului in litere mici inainte de a cauta in dictionar
                        String priceSelector = STORE_SELECTORS.get(storeName.toLowerCase().trim());

                        if (priceSelector == null) {
                            LOG.warning("Magazin necunoscut in dictionar: " + storeName + " pentru linkul: " + link.getUrl());
                            continue;
                        }

                        page.waitForSelector(priceSelector, new Page.WaitForSelectorOptions().setTimeout(10000));
                        String priceText = page.locator(priceSelector).first().innerText();

                        Double noulPret = parsePriceText(priceText);

                        if (noulPret != null) {
                            link.setLastPrice(noulPret);
                            link.setLastChecked(LocalDateTime.now());
                            em.merge(link);

                            PriceHistory history = new PriceHistory();
                            history.setProductLink(link);
                            history.setPrice(noulPret);
                            history.setRecordedAt(LocalDateTime.now());
                            em.persist(history);

                            Products product = link.getProduct();
                            if (product != null) {
                                Double curentAllTimeLow = product.getAll_time_low();
                                if (curentAllTimeLow == null || noulPret < curentAllTimeLow) {
                                    LOG.info("Pret nou record gasit. Actualizam all_time_low.");
                                    product.setAll_time_low(noulPret);
                                    em.merge(product);
                                }
                            }
                            LOG.info("Verificare finalizata cu succes pentru: " + link.getUrl());
                        }

                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Eroare la procesarea link-ului: " + link.getUrl(), e.getMessage());
                    }
                }
            }

            LOG.info("Procesul de verificare s-a incheiat pentru toate produsele.");

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Eroare la pornirea playwright.", e.getMessage());
        }
    }
}