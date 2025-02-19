package com.hsbc.wpb.cmt.core.avaloq.messaging.adaptor.btf.config;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
public class MQMessage {
    private String productType;
    private String content;

    @XmlElement
    public String getProductType() {
        return productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }

    @XmlElement
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MQMessage{productType='" + productType + "', content='" + content + "'}";
    }
}
