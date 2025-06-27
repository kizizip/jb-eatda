package com.jbeatda.DTO.responseDTO;

import com.jbeatda.domain.stores.entity.Bookmark;
import com.jbeatda.exception.ApiResult;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkListResponseDTO implements ApiResult {

    private List<BookmarkItemDTO> bookmarks;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookmarkItemDTO {
        private Integer storeId;
        private String sno;
        private String storeName;
        private String address;
        private String smenu;
    }

    public static BookmarkListResponseDTO fromBookmarks(List<Bookmark> bookmarks) {
        List<BookmarkItemDTO> bookmarkItems = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            bookmarkItems.add(bookmark.toBookmarkItemDTO(bookmark));
        }

        return BookmarkListResponseDTO.builder()
                .bookmarks(bookmarkItems)
                .build();
    }

}