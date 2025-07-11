package org.kosa.tripTalk.bookmark;

import lombok.Data;

@Data
public class BookmarkLinkRequest {
    private Long boardId;
    private String tempKey;
}