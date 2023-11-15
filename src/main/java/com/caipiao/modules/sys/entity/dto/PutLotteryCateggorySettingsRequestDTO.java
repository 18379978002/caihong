package com.caipiao.modules.sys.entity.dto;

import com.caipiao.modules.sys.entity.enmu.LotteryCategory;
import lombok.Data;

@Data
public class PutLotteryCateggorySettingsRequestDTO {
    LotteryCategory lotteryCategory;
    String fieldName;
    String fieldValue;
}
