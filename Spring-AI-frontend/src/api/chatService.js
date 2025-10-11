import axios from 'axios';

/**
 * 发送普通聊天消息请求
 * @param {string} message - 用户输入的消息
 * @param {string} model - 选择的AI模型 (qwen 或 deepseek)
 * @returns {Promise<string>} - AI返回的响应内容
 */
export const sendChatMessage = async (message, model) => {
  const endpoint = model === 'qwen' 
    ? '/api/ai/qwenClientChat' 
    : '/api/ai/clientChat';
  
  const response = await axios.get(endpoint, {
    params: { msg: message }
  });
  
  return response.data;
};

/**
 * 发送流式聊天消息请求
 * @param {string} message - 用户输入的消息
 * @param {string} model - 选择的AI模型 (qwen 或 deepseek)
 * @param {Function} onChunkReceived - 接收流式数据块的回调函数
 * @returns {Promise<void>}
 */
export const sendStreamingChatMessage = async (message, model, onChunkReceived) => {
  const endpoint = model === 'qwen' 
    ? '/api/ai/qwenClientStream' 
    : '/api/ai/clientStream';
  
  const response = await axios.get(endpoint, {
    params: { msg: message },
    responseType: 'stream'
  });

  return new Promise((resolve, reject) => {
    const stream = response.data;
    const decoder = new TextDecoder();
    
    stream.on('data', (chunk) => {
      const text = decoder.decode(chunk, { stream: true });
      onChunkReceived(text);
    });
    
    stream.on('end', () => {
      resolve();
    });
    
    stream.on('error', (err) => {
      reject(new Error(`Stream error: ${err.message}`));
    });
  });
};
