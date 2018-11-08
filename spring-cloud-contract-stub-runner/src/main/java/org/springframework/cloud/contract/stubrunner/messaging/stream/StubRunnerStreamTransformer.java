/*
 *  Copyright 2013-2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.cloud.contract.stubrunner.messaging.stream;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.verifier.util.BodyExtractor;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Sends forward a message defined in the DSL.
 *
 * @author Marcin Grzejszczak
 */
class StubRunnerStreamTransformer implements GenericTransformer<Message<?>, Message<?>> {

	private final StubRunnerStreamMessageSelector selector;

	StubRunnerStreamTransformer(Contract groovyDsl) {
		this(Collections.singletonList(groovyDsl));
	}

	StubRunnerStreamTransformer(List<Contract> groovyDsls) {
		this.selector = new StubRunnerStreamMessageSelector(groovyDsls);
	}

	@Override
	public Message<?> transform(Message<?> source) {
		Contract groovyDsl = matchingContract(source);
		if (groovyDsl == null || groovyDsl.getOutputMessage() == null) {
			return source;
		}
		String payload = BodyExtractor
				.extractStubValueFrom(groovyDsl.getOutputMessage().getBody());
		Map<String, Object> headers = groovyDsl.getOutputMessage().getHeaders()
				.asStubSideMap();
		MessageHeaders messageHeaders = new MessageHeaders(headers);
		Message<String> message = MessageBuilder.createMessage(payload,
				messageHeaders);
		this.selector.updateCache(message, groovyDsl);
		return message;
	}

	Contract matchingContract(Message<?> source) {
		return this.selector.matchingContract(source);
	}

}
