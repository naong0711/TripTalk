package org.kosa.tripTalk.bookmark;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookMarkResponseDTO {
    private Long id;
    private String placeName;
    private String address;
    private double latitude;
    private double longitude;
    private String tempKey;
}