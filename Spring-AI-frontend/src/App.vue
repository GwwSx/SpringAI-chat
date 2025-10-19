<template>
  <div class="app-container">
    <!-- 顶部模型选择器 -->
    <div class="model-selector">
      <select v-model="selectedModel" @change="onModelChange">
        <option value="qwen">Qwen</option>
        <option value="deepseek">DeepSeek</option>
      </select>
    </div>
    
    <!-- 聊天内容区域 -->
    <div class="chat-container">
      <div class="chat-messages">
        <div class="message" v-for="(msg, index) in messages" :key="index" :class="msg.role">
          <div class="avatar">
            <svg v-if="msg.role === 'user'" viewBox="0 0 100 100" width="40" height="40" class="user-avatar">
              <circle cx="50" cy="50" r="45" fill="#0078ff"/>
              <path d="M35,60 Q50,35 65,60" stroke="white" stroke-width="8" fill="none"/>
              <path d="M30,80 Q50,65 70,80" stroke="white" stroke-width="6" fill="none"/>
            </svg>
            <svg v-else-if="msg.role === 'assistant'" viewBox="0 0 100 100" width="40" height="40" class="assistant-avatar">
              <circle cx="50" cy="50" r="45" fill="#42b983"/>
              <rect x="30" y="35" width="40" height="30" rx="15" fill="white"/>
              <circle cx="45" cy="50" r="5" fill="#333"/>
              <circle cx="55" cy="50" r="5" fill="#333"/>
              <path d="M40,65 Q50,75 60,65" stroke="#333" stroke-width="3" fill="none"/>
            </svg>
          </div>
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-meta">{{ msg.timestamp }}</div>
        </div>
        <div v-if="isLoading" class="loading-indicator">
          <div class="spinner"></div>
          <span>AI正在思考...</span>
        </div>
      </div>
    </div>
    
    <!-- 输入区域 -->
    <div class="input-area">
      <div class="thinking-mode">
        <span>🧠</span>
        <span>深度思考:自动</span>
      </div>
      
      <div class="input-container">
        <textarea 
          v-model="inputMessage" 
          @keydown.enter.prevent="sendMessage"
          @input="autoResizeTextarea"
          placeholder="发消息、输入@选择技能或/选择文件"
          :disabled="isLoading"
          :style="{ height: textareaHeight + 'px' }"
        ></textarea>
        
        <div class="input-actions">
          <button class="action-btn" title="剪切"><i class="icon-scissors">✂️</i></button>
          <button class="action-btn" title="语音"><i class="icon-mic">📞</i></button>
          <button class="action-btn" title="麦克风"><i class="icon-microphone">🎤</i></button>
          <button class="send-btn" @click="sendMessage" :disabled="isLoading || !inputMessage.trim()">
            <i class="icon-send">↑</i>
          </button>
        </div>
      </div>
      
      <!-- 功能按钮 -->
      <div class="function-buttons">
        <button class="function-btn" @click="showFeatureNotImplemented">AI编程</button>
        <button class="function-btn" @click="showFeatureNotImplemented">AI阅读</button>
        <button class="function-btn" @click="showFeatureNotImplemented">帮我写作</button>
        <button class="function-btn" @click="showFeatureNotImplemented">图像生成</button>
        <button class="function-btn" @click="showFeatureNotImplemented">AI搜索</button>
        <button class="function-btn" @click="showFeatureNotImplemented">AI PPT</button>
        <button class="function-btn more-btn" @click="showFeatureNotImplemented">更多</button>
      </div>
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

const textareaHeight = ref(60); // 默认高度

const autoResizeTextarea = () => {
  // 重置高度以获得正确的scrollHeight
  textareaHeight.value = 60;
  // 获取textarea元素
  const textarea = document.querySelector('.input-container textarea');
  if (textarea) {
    // 设置最小高度为60px，最大高度为200px
    const newHeight = Math.min(Math.max(textarea.scrollHeight, 60), 200);
    textareaHeight.value = newHeight;
  }
};

const showFeatureNotImplemented = () => {
  messages.value.push({
    role: 'system',
    content: '功能待完善中...',
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
/* 基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 20px;
  background-color: #ffffff;
  overflow: hidden;
}

.chat-container {
  flex: 1;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 0; /* 修复flex容器溢出问题 */
  background-color: #ffffff; /* 添加背景色确保内容可见 */
}

.chat-messages {
  width: 100%;
  display: flex;
  flex-direction: column;
}

/* 模型选择器 */
.model-selector {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.model-selector select {
  padding: 6px 10px;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
  background-color: white;
}

/* 主内容区 */

.message {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 18px;
  margin-bottom: 15px;
  position: relative;
  word-break: break-word;
  box-sizing: border-box; /* 确保内边距不会导致溢出 */
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.user-avatar {
  background-color: #0078ff;
}

.assistant-avatar {
  background-color: #42b983;
}

.user .message-content {
  margin-left: 5px;
}

.assistant .message-content {
  margin-right: 5px;
}

.message-content {
  min-height: 20px;
  max-height: 500px;
  overflow-y: auto;
}

.user {
  margin-left: auto;
  background-color: #0078ff;
  color: white;
}

.assistant {
  background-color: #f0f0f0;
  color: #333333;
}

.system {
  align-self: center;
  background-color: #e9e9e9; /* 更深的灰色背景 */
  font-size: 0.9rem;
  color: #666666;
  max-width: 50%;
  text-align: center;
  margin: 8px auto;
  padding: 10px 20px;
  border-radius: 12px; /* 更圆润的边角 */
  box-shadow: 0 1px 2px rgba(0,0,0,0.05); /* 轻微阴影增加层次感 */
}

.message-meta {
  font-size: 0.7rem;
  opacity: 0.7;
  margin-top: 5px;
  text-align: right;
}

/* 输入区域 */
.input-area {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: white;
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.thinking-mode {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 4px 10px;
  background-color: #e6f7ff;
  color: #1890ff;
  border-radius: 12px;
  font-size: 14px;
  margin-bottom: 10px;
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.input-container textarea {
  flex: 1;
  padding: 15px 20px;
  padding-right: 100px;
  border: 1px solid #e0e0e0;
  border-radius: 24px;
  resize: none;
  min-height: 60px;
  max-height: 200px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s;
}

.input-container textarea:focus {
  border-color: #0078ff;
}

.input-actions {
  position: absolute;
  right: 15px;
  display: flex;
  gap: 10px;
}

.action-btn, .send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background-color: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.action-btn:hover, .send-btn:hover {
  background-color: #f0f0f0;
}

.send-btn {
  background-color: #0078ff;
  color: white;
}

.send-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 功能按钮 */
.function-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 15px;
  flex-wrap: wrap;
}

.function-btn {
  padding: 6px 16px;
  background-color: #f5f5f5;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  color: #333333;
  transition: all 0.2s;
}

.function-btn:hover {
  background-color: #e0e0e0;
}

.more-btn {
  color: #666666;
}

/* 加载指示器 */
.loading-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #666;
  padding: 10px;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #0078ff;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
