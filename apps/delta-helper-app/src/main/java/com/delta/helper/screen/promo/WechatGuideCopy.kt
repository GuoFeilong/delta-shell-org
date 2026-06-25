package com.delta.helper.screen.promo

object WechatGuideCopy {
    const val DIALOG_TITLE = "关注公众号"
    const val DIALOG_SUBTITLE = "微信搜一搜「画质怪兽」获取教程与帮助"
    const val DISMISS_BUTTON = "我知道了"
    const val SAVING_BUTTON = "保存中..."
    const val IMAGE_CONTENT_DESCRIPTION = "微信公众号画质怪兽引导图"
    const val SAVED_TO_ALBUM = "已保存到相册"
    const val ALBUM_FILE_NAME = "画质怪兽_微信公众号.jpg"

    fun purchaseOpenedMessage(browserOpened: Boolean): String =
        if (browserOpened) {
            "购买页已打开，付完款请返回激活"
        } else {
            "链接已复制，请在浏览器购买"
        }
}
