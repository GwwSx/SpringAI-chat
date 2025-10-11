<template>
  <div class="chat-app">
    <div class="chat-header">
      <h1>Spring AI 聊天平台</h1>
      <div class="model-selector">
        <label for="model">选择模型:</label>
        <select v-model="selectedModel" id="model" @change="onModelChange">
          <option value="qwen">Qwen</option>
          <option value="deepseek">DeepSeek</option>
        </select>
      </div>
    </div>
    
    <div class="chat-messages">
      <div class="message" v-for="(msg, index) in messages" :key="index" :class="msg.role">
        <div class="message-content">{{ msg.content }}</div>
        <div class="message-meta">{{ msg.timestamp }}</div>
      </div>
      <div v-if="isLoading" class="loading-indicator">
        <div class="spinner"></div>
        <span>AI正在思考...</span>
      </div>
    </div>
    
    <div class="chat-input">
      <textarea 
        v-model="inputMessage" 
        @keydown.enter.prevent="sendMessage"
        placeholder="输入消息...按Enter发送"
        :disabled="isLoading"
      ></textarea>
      <button @click="sendMessage" :disabled="isLoading || !inputMessage.trim()">
        发送
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { sendChatMessage, sendStreamingChatMessage } from './api/chatService';

const messages = ref([]);
const inputMessage = ref('');
const isLoading = ref(false);
const selectedModel = ref('qwen');
const isStreaming = ref(true);

onMounted(() => {
  // 初始化欢迎消息
  messages.value.push({
    role: 'assistant',
    content: '你好！我是AI助手，有什么可以帮助你的吗？',
    timestamp: new Date().toLocaleTimeString()
  });
});

const sendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message) return;
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: message,
    timestamp: new Date().toLocaleTimeString()
  });
  
  inputMessage.value = '';
  isLoading.value = true;
  
  try {
    if (isStreaming.value) {
      // 处理流式响应
      const assistantMessageId = messages.value.length;
      messages.value.push({
        role: 'assistant',
        content: '',
        timestamp: new Date().toLocaleTimeString()
      });
      
      await sendStreamingChatMessage(message, selectedModel.value, (chunk) => {
        messages.value[assistantMessageId].content += chunk;
      });
    } else {
      // 处理普通响应
      const response = await sendChatMessage(message, selectedModel.value);
      messages.value.push({
        role: 'assistant',
        content: response,
        timestamp: new Date().toLocaleTimeString()
      });
    }
  } catch (error) {
    messages.value.push({
      role: 'assistant',
      content: `发生错误: ${error.message}`,
      timestamp: new Date().toLocaleTimeString()
    });
  } finally {
    isLoading.value = false;
    // 滚动到底部
    scrollToBottom();
  }
};

const onModelChange = () => {
  messages.value.push({
    role: 'system',
    content: `已切换到${selectedModel.value === 'qwen' ? 'Qwen' : 'DeepSeek'}模型`,
    timestamp: new Date().toLocaleTimeString()
  });
  scrollToBottom();
};

const scrollToBottom = () => {
  const container = document.querySelector('.chat-messages');
  container.scrollTop = container.scrollHeight;
};
</script>

<style scoped>
.chat-app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 800px;
  margin: 0 auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.chat-header {
  background-color: #42b983;
  color: white;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.model-selector {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.model-selector select {
  padding: 0.3rem;
  border-radius: 4px;
  border: none;
}

.chat-messages {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  background-color: #f9f9f9;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message {
  max-width: 70%;
  padding: 0.8rem;
  border-radius: 8px;
  position: relative;
}

.user {
  align-self: flex-end;
  background-color: #42b983;
  color: white;
}

.assistant {
  align-self: flex-start;
  background-color: white;
  border: 1px solid #e0e0e0;
}

.system {
  align-self: center;
  background-color: #e8f5e9;
  font-size: 0.9rem;
  color: #555;
}

.message-meta {
  font-size: 0.7rem;
  opacity: 0.7;
  margin-top: 0.3rem;
  text-align: right;
}

.chat-input {
  display: flex;
  padding: 1rem;
  background-color: white;
  border-top: 1px solid #e0e0e0;
  gap: 0.5rem;
}

.chat-input textarea {
  flex: 1;
  padding: 0.8rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: none;
  min-height: 60px;
}

.chat-input button {
  padding: 0.8rem 1.5rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-input button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.loading-indicator {
  align-self: flex-start;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #42b983;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
