// 简单的二维码生成工具
// 这里使用canvas绘制一个简单的二维码样式

/**
 * 生成二维码
 * @param {string} text - 要编码的文本
 * @param {Object} canvas - canvas对象
 * @param {number} size - 二维码尺寸
 */
function generateQRCode(text, canvas, size = 200) {
  const ctx = canvas.getContext('2d')
  
  // 清空画布
  ctx.clearRect(0, 0, size, size)
  
  // 设置背景为白色
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, size, size)
  
  // 绘制简单的二维码样式
  ctx.fillStyle = '#000000'
  
  // 绘制边框
  const borderSize = 10
  ctx.fillRect(0, 0, size, borderSize) // 上边框
  ctx.fillRect(0, 0, borderSize, size) // 左边框
  ctx.fillRect(size - borderSize, 0, borderSize, size) // 右边框
  ctx.fillRect(0, size - borderSize, size, borderSize) // 下边框
  
  // 绘制三个角的定位点
  const cornerSize = 30
  const innerSize = 10
  
  // 左上角
  ctx.fillRect(borderSize, borderSize, cornerSize, cornerSize)
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(borderSize + 5, borderSize + 5, cornerSize - 10, cornerSize - 10)
  ctx.fillStyle = '#000000'
  ctx.fillRect(borderSize + 10, borderSize + 10, innerSize, innerSize)
  
  // 右上角
  ctx.fillRect(size - borderSize - cornerSize, borderSize, cornerSize, cornerSize)
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(size - borderSize - cornerSize + 5, borderSize + 5, cornerSize - 10, cornerSize - 10)
  ctx.fillStyle = '#000000'
  ctx.fillRect(size - borderSize - cornerSize + 10, borderSize + 10, innerSize, innerSize)
  
  // 左下角
  ctx.fillRect(borderSize, size - borderSize - cornerSize, cornerSize, cornerSize)
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(borderSize + 5, size - borderSize - cornerSize + 5, cornerSize - 10, cornerSize - 10)
  ctx.fillStyle = '#000000'
  ctx.fillRect(borderSize + 10, size - borderSize - cornerSize + 10, innerSize, innerSize)
  
  // 绘制一些随机的数据点（模拟二维码数据）
  ctx.fillStyle = '#000000'
  const dataSize = 4
  const startX = borderSize + cornerSize + 10
  const startY = borderSize + 10
  const endX = size - borderSize - cornerSize - 10
  const endY = size - borderSize - 10
  
  // 基于文本内容生成伪随机点
  let seed = 0
  for (let i = 0; i < text.length; i++) {
    seed += text.charCodeAt(i)
  }
  
  function pseudoRandom() {
    seed = (seed * 9301 + 49297) % 233280
    return seed / 233280
  }
  
  for (let x = startX; x < endX; x += dataSize + 2) {
    for (let y = startY; y < endY; y += dataSize + 2) {
      if (pseudoRandom() > 0.5) {
        ctx.fillRect(x, y, dataSize, dataSize)
      }
    }
  }
  
  // 在中心绘制文本信息
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(size/2 - 40, size/2 - 10, 80, 20)
  ctx.fillStyle = '#000000'
  ctx.font = '12px Arial'
  ctx.textAlign = 'center'
  ctx.fillText('通行码', size/2, size/2 + 5)
}

/**
 * 将canvas转换为图片
 * @param {Object} canvas - canvas对象
 * @returns {Promise<string>} - 图片临时路径
 */
function canvasToImage(canvas) {
  return new Promise((resolve, reject) => {
    wx.canvasToTempFilePath({
      canvas: canvas,
      success: (res) => {
        resolve(res.tempFilePath)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

module.exports = {
  generateQRCode,
  canvasToImage
}




