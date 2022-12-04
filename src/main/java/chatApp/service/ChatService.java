package chatApp.service;

import chatApp.Entities.ChatMessage;
import chatApp.repository.MassegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    MassegeRepository massegeRepository;


    public ChatService(MassegeRepository massegeRepository) {
        this.massegeRepository = massegeRepository;
    }

    public List<ChatMessage> getAllMessagesByChatId(String chatId) {
        List<ChatMessage> list = massegeRepository.findAll().stream()
                .filter(message -> message.getChatId().equals(chatId))
                .sorted(Comparator.comparing(ChatMessage::getDateMessage))
                .collect(Collectors.toList());

        return list;
    }

    public void saveMessagesToDB(ChatMessage message) {
        massegeRepository.save(message);
    }


    public List<ChatMessage> allMessageBetween_A_B(String sender, String destination) {
        List<ChatMessage> allMessage = massegeRepository.findAll();
        List<ChatMessage> newChat = null;

        for (ChatMessage lst : allMessage)
            if (lst.getSender() == sender && lst.getChatId() == destination)
                newChat.add(lst);

        return newChat;
    }
}

