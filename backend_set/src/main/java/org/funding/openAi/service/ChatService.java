package org.funding.openAi.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.funding.openAi.client.OpenAIClient;
import org.funding.openAi.dao.ChatDAO;
import org.funding.openAi.dto.ChatRequestDTO;
import org.funding.openAi.dto.ChatResponseDTO;
import org.funding.openAi.vo.ChatLogVO;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenAIClient openAIClient;

    private final ChatDAO chatDAO;

    public ChatResponseDTO ask(ChatRequestDTO chatRequestDTO) {
        String prompt = chatRequestDTO.getPrompt();
        String answer = openAIClient.askOpenAI(prompt);

        ChatLogVO chatLog = new ChatLogVO();
        chatLog.setPrompt(prompt);
        chatLog.setResponse(answer);
        chatLog.setCreateAt(new Date());

        chatDAO.insertChatLog(chatLog);

        return new ChatResponseDTO(answer);
    }
}
