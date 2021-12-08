package hu.rhalm.wasteless.feed.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
@AllArgsConstructor
@Getter
@Setter
public class SearchParams {
    private String searchTerm;
    private Set<String> locations;
    private Set<String> categories;
    private boolean withImageOnly;
    private String userId;
}

