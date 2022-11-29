package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.service.ChatService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private UserService userService;
    @Autowired
    ChatService chatService;

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public RecievedMessage greeting(HelloMessage message) {
        return new RecievedMessage("SYSTEM", message.getName() + "joined the chat");
    }

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public RecievedMessage sendPlainMessage(RecievedMessage message) {
        System.out.println(message);

        if (message.token == null)
            return null;

        if (!userService.isUserMuted(message.token)) {
            ChatMessage chatMessage = new ChatMessage(message.getSender(), message.getContent(), message.getChatId());
            chatService.saveMessagesToDB(chatMessage);
            return message;
        }
        return null;
    }



    static class RecievedMessage {
        private String sender;
        private String content;
        private String token;
        private String chatId;

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

        public RecievedMessage() {
        }

        public RecievedMessage(String sender, String content) {
            this.sender = sender;
            this.content = content;
            this.chatId = chatId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "ChatMessage{" +
                    "sender='" + sender + '\'' +
                    ", content='" + content + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }

    static class HelloMessage {

        private String name;

        public HelloMessage() {
        }

        public HelloMessage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "HelloMessage{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}