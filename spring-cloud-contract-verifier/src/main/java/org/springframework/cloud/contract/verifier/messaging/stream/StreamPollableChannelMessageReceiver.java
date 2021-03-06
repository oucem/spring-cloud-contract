/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.contract.verifier.messaging.stream;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;

class StreamPollableChannelMessageReceiver
		implements MessageVerifierReceiver<Message<?>> {

	private static final Log log = LogFactory
			.getLog(StreamPollableChannelMessageReceiver.class);

	private final ApplicationContext context;

	private final DestinationResolver destinationResolver;

	StreamPollableChannelMessageReceiver(ApplicationContext context) {
		this.context = context;
		this.destinationResolver = new DestinationResolver(context);
	}

	@Override
	public Message<?> receive(String destination, long timeout, TimeUnit timeUnit) {
		try {
			PollableChannel messageChannel = this.context.getBean(this.destinationResolver
					.resolvedDestination(destination, DefaultChannels.INPUT),
					PollableChannel.class);
			return messageChannel.receive(timeUnit.toMillis(timeout));
		}
		catch (Exception e) {
			log.error("Exception occurred while trying to read a message from "
					+ " a channel with name [" + destination + "]", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Message<?> receive(String destination) {
		return receive(destination, 5, TimeUnit.SECONDS);
	}

}
