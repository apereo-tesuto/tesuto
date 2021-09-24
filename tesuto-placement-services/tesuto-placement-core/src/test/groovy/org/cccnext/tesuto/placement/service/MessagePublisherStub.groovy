package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.message.service.MessagePublisher

public class MessagePublisherStub implements MessagePublisher<Object> {
    def lastMessage = null

    public void sendMessage(Object messageObject) {
        lastMessage = messageObject
    }
}
