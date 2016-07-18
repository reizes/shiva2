package com.reizes.shiva2.kafka.loader;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.loader.AbstractLoader;

public class KafkaProduceLoader extends AbstractLoader implements AfterProcessAware {
	private KafkaProducerHelper producerHelpder;
	private String topic;
	private String messageKeyName;
	private String messageKey = null;	// static message key
	private Gson gson;

	public KafkaProduceLoader(String topic, Map<java.lang.String, java.lang.Object> configs) {
		producerHelpder = new KafkaProducerHelper(configs);
		this.topic = topic;
		gson = new Gson();
	}

	public KafkaProduceLoader(String topic, String messageKeyName, Map<java.lang.String, java.lang.Object> configs) {
		producerHelpder = new KafkaProducerHelper(configs);
		this.topic = topic;
		this.messageKeyName = messageKeyName;
		gson = new Gson();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		if (input != null) {
			if (input instanceof String) {
				producerHelpder.send(this.topic, this.messageKey, (String)input);
			} else if (input instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) input;
				String messageKey = null;
				if (messageKeyName!=null) {
					messageKey = map.get(messageKeyName).toString();
				}
				String message = StringUtils.removePattern(gson.toJson(map), "[\r\n\t]");
				producerHelpder.send(this.topic, messageKey, message);
			}
		}
		return input;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		producerHelpder.close();
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessageKeyName() {
		return messageKeyName;
	}

	public void setMessageKeyName(String messageKeyName) {
		this.messageKeyName = messageKeyName;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

}
