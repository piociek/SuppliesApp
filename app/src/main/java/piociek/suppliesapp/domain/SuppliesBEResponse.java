package piociek.suppliesapp.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SuppliesBEResponse {
    private boolean success;
    private String message;
    private List<Item> items;
}
