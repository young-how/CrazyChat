package org.DUT.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class configParam {
    private String mediaPath;
    private String topicName;
    private String IP;
    private String serverPort;
    private String Name;
    private String kafkaPort;
}
