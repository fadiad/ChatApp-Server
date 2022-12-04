package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.HelloMessage;
import chatApp.Entities.RecievedMessage;
import chatApp.service.ChatService;
import chatApp.service.UserService;
import net.bytebuddy.utility.JavaConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
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

    @Autowired
    private SimpMessagingTemplate messageSender;

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public RecievedMessage greeting(HelloMessage message) {
        return new RecievedMessage("SYSTEM", message.getName() + "joined the chat");
    }

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public RecievedMessage sendPlainMessage(RecievedMessage message) {
        System.out.println(message);

        if (message.getToken() == null)
            return null;

        if (!userService.isUserMuted(message.getToken())) {
            ChatMessage chatMessage = new ChatMessage(message.getSender(), message.getContent(), message.getChatId());
            chatService.saveMessagesToDB(chatMessage);
            return message;
        }
        return null;
    }

    @MessageMapping("/private-message")
    public RecievedMessage sendPlainMessagePrivate(@Payload RecievedMessage message) {
        System.out.println("------------sendPlainMessagePrivate--------------");
        System.out.println(message);
        if (message.getToken() == null)
            return null;
        ChatMessage chatMessage = new ChatMessage(message.getSender(), message.getContent(), message.getChatId());
        chatService.saveMessagesToDB(chatMessage);
        messageSender.convertAndSendToUser(message.getChatId() ,"/private", message);
        return message;
    }

}