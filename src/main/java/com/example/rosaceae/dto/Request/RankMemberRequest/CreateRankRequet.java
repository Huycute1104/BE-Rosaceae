package com.example.rosaceae.dto.Request.RankMemberRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRankRequet {
    private String rankName;
    private int rankPoint;
}
