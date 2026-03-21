package com.pricewatch.pricewatch.common;
import java.time.LocalDateTime;
import java.util.List;

public class ProductLinkDto {

    private Long id;
    private String storeName;
    private String url;
    private Double lastPrice;
    private LocalDateTime lastChecked;

    public ProductLinkDto(Long id, String storeName, String url, Double lastPrice, LocalDateTime lastChecked) {
        this.id = id;
        this.storeName = storeName;
        this.url = url;
        this.lastPrice = lastPrice;
        this.lastChecked = lastChecked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }

    // aceasta lista stocheaza toate fluctuatiile de pret din trecut pentru acest link/magazin specific.
    // este absolut necesara pentru frontend fara ea, am sti doar pretul de azi,
    // iar pagina jsp  nu ar avea punctele de date necesare pentru a desena graficul evolutiei in timp.
    private List<ProductHistoryDto> priceHistories;
    public List<ProductHistoryDto> getPriceHistories() {
        return priceHistories;
    }

    public void setPriceHistories(List<ProductHistoryDto> priceHistories) {
        this.priceHistories = priceHistories;
    }
}
