package com.fr20en.activation.data

import com.fr20en.activation.model.ActivationCode
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CodeGenerator {
    
    // AES-ECB 密钥 (与 Python 脚本相同)
    private const val AES_KEY = "Format2044153997"
    
    /**
     * 生成激活码
     * @param machineCode 机器码 (纯数字)
     * @param durationMs 激活时长 (毫秒)
     * @return ActivationCode 包含生成的激活码或错误信息
     */
    fun generate(machineCode: String, durationMs: Long): ActivationCode {
        return try {
            // 验证机器码是否为纯数字
            val mc = machineCode.toLongOrNull() 
                ?: throw IllegalArgumentException("机器码必须是纯数字")
            
            // 计算中间值 (与 Python 脚本逻辑相同)
            val vv = (mc / 2) + 37915
            
            // 构建明文: vv的16进制 + "z" + 时长毫秒数
            val plain = "${vv.toString(16)}z$durationMs"
            
            // AES-ECB 加密
            val keySpec = SecretKeySpec(AES_KEY.toByteArray(Charsets.UTF_8), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            
            val encrypted = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
            val code = encrypted.joinToString("") { "%02X".format(it) }
            
            ActivationCode(
                id = System.currentTimeMillis(),
                appName = "天一科技",
                code = code,
                machineCode = machineCode,
                durationMs = durationMs,
                createdAt = System.currentTimeMillis(),
                error = null
            )
        } catch (e: Exception) {
            ActivationCode(
                id = System.currentTimeMillis(),
                appName = "天一科技",
                code = "",
                machineCode = machineCode,
                durationMs = durationMs,
                createdAt = System.currentTimeMillis(),
                error = e.message ?: "未知错误"
            )
        }
    }
}
