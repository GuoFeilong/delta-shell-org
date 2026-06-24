package com.delta.helper.screen.legal

enum class LegalDocType(val routeKey: String) {
    User("user"),
    Privacy("privacy"),
    Payment("payment"),
    ;

    companion object {
        fun fromRouteKey(key: String?): LegalDocType =
            entries.firstOrNull { it.routeKey == key } ?: User
    }
}

data class LegalDocMeta(
    val title: String,
    val subtitle: String,
)

data class LegalSection(
    val heading: String,
    val paragraphs: List<String>,
)

object LegalCopy {
    const val AGREEMENT_VERSION = "v2"
    const val UPDATED_AT = "2025年6月"

    const val PRODUCT_NATURE_SHORT =
        "本产品为第三方玩家参考工具，提供画质与帧率设置建议、方案查询与教程指引。" +
        "不会修改手机硬件、系统或游戏客户端，需您在游戏中自行手动调整。"

    const val SERVICE_NATURE_ACK =
        "我理解：内容访问码仅开通本 App 内参考内容访问，不会自动修改游戏或提升帧率"

    const val PURCHASE_CONFIRM_TITLE = "购卡须知"
    const val PURCHASE_CONFIRM_CONTENT =
        "您将购买的是「本 App 内参考内容访问码」，不是游戏内自动改帧或性能提升服务。\n\n" +
        "· 具体价格以购买页展示为准\n" +
        "· 方案需您在游戏中自行手动设置\n" +
        "· 实际帧率由手机芯片、散热、游戏版本、网络等因素决定，本 App 无法保证达到所选帧率\n" +
        "· 访问码一经使用通常不可退换，请妥善保管\n" +
        "· 购买链接将在浏览器打开，请认准官方渠道，谨防仿站\n" +
        "· 未满 18 周岁请在监护人同意下购买\n\n" +
        "如有疑问，可在「我的-意见反馈」选择「激活/购卡问题」。"

    const val CARD_ACTIVATE_CONFIRM_TITLE = "激活须知"
    const val CARD_ACTIVATE_CONFIRM_CONTENT =
        "内容访问码用于解锁本 App 内的参考内容与功能访问权限。\n\n" +
        "· 不会自动修改游戏设置或提升帧率\n" +
        "· 请在游戏内对照方案手动调整\n" +
        "· 每张访问码通常仅可使用一次\n\n" +
        "激活即表示您已阅读并同意《付费与激活说明》。"

    const val FIRST_LAUNCH_TITLE = "欢迎使用画质怪兽2026"
    const val FIRST_LAUNCH_SUBTITLE = "使用前请阅读并确认以下内容"
    val FIRST_LAUNCH_SUMMARY_BULLETS = listOf(
        "本 App 为第三方参考工具，提供画质与帧率设置建议、方案查询与教程指引，不会自动修改游戏或手机硬件。",
        "实际画面与帧率由手机芯片、散热、游戏版本、网络等因素决定，本 App 无法保证达到所选帧率。",
        "「内容访问码」仅用于解锁 App 内参考内容访问，不是游戏内改帧或性能提升服务。",
        "我们将按《隐私政策》处理设备信息等数据，用于激活校验与产品改进。",
    )
    const val FIRST_LAUNCH_PAYMENT_HINT = "购卡、激活与退款规则详见《付费与激活说明》。"
    const val FIRST_LAUNCH_LEGAL_PREFIX = "我已阅读并同意"
    const val FIRST_LAUNCH_AGREE = "同意并继续"
    const val FIRST_LAUNCH_DECLINE = "不同意并退出"
    const val FIRST_LAUNCH_VALIDATION_ERROR = "请先阅读并同意相关协议，并确认已理解服务性质"

    val docMeta: Map<LegalDocType, LegalDocMeta> = mapOf(
        LegalDocType.User to LegalDocMeta("用户服务协议", "USER AGREEMENT"),
        LegalDocType.Privacy to LegalDocMeta("隐私政策", "PRIVACY POLICY"),
        LegalDocType.Payment to LegalDocMeta("付费与激活说明", "PAYMENT & ACTIVATION"),
    )

    fun relatedLinks(current: LegalDocType): List<Pair<LegalDocType, String>> =
        listOf(
            LegalDocType.User to "用户协议",
            LegalDocType.Privacy to "隐私政策",
            LegalDocType.Payment to "付费说明",
        ).filter { it.first != current }

    fun document(type: LegalDocType): List<LegalSection> = when (type) {
        LegalDocType.User -> userAgreement
        LegalDocType.Privacy -> privacyPolicy
        LegalDocType.Payment -> paymentTerms
    }

    private val userAgreement = listOf(
        LegalSection(
            heading = "一、服务性质",
            paragraphs = listOf(
                "「画质怪兽」（以下简称「本产品」）是由运营方提供的第三方玩家参考工具，与《三角洲行动》及任何游戏开发商、发行商、官方平台均无隶属、授权或合作关系。",
                "本产品向用户提供手游画质与帧率相关的设置参考、机型方案查询、教程指引、灵敏度及社区信息整理等服务。",
                "本产品不能也不会修改您的手机硬件、操作系统、游戏客户端或游戏数据。所有游戏内设置均需您自行手动完成。",
                "通过快捷方式启动游戏仅为便捷入口，不代表本产品在后台修改了游戏内任何参数。",
            ),
        ),
        LegalSection(
            heading = "二、无效果保证",
            paragraphs = listOf(
                "您理解并同意：本产品提供的方案、参数、教程等内容仅供参考，不构成对帧率、画质、流畅度、网络延迟或游戏排名的任何承诺或保证。",
                "实际效果受设备型号、系统版本、游戏版本、网络环境及个人操作等因素影响，运营方不对因使用或未能使用本产品而产生的任何直接或间接损失承担责任（法律另有强制性规定的除外）。",
            ),
        ),
        LegalSection(
            heading = "三、悬浮窗与方案展示",
            paragraphs = listOf(
                "本产品可能在您授权后展示悬浮窗，用于显示您所选游戏的参考方案信息（如目标帧率档位、分辨率参考、设备内存等）。",
                "悬浮窗中的数值与文案均为参考方案或设备信息展示，不代表游戏内已生效或已达到相应帧率。",
                "您可随时关闭悬浮窗权限或在本产品内停止相关功能。",
            ),
        ),
        LegalSection(
            heading = "四、账号与内容访问",
            paragraphs = listOf(
                "您可通过完成任务或输入有效内容访问码（激活码）的方式，开通本产品内相应参考内容的访问权限。",
                "「激活」「开通」均指本产品内的数字化内容访问权限，不包含任何游戏内道具、会员或官方服务。",
                "运营方有权根据业务需要调整免费与付费内容的范围，并将在合理范围内通过应用内公告等方式告知。",
            ),
        ),
        LegalSection(
            heading = "五、用户行为规范",
            paragraphs = listOf(
                "您不得利用本产品从事违法违规活动，包括但不限于传播外挂、作弊工具信息，侵犯他人知识产权，或发布虚假、误导性内容。",
                "您不得将本产品用于规避游戏用户协议或平台规则的行为。",
                "您应妥善保管内容访问码及设备信息，因个人泄露、转售访问码导致的损失由您自行承担。",
            ),
        ),
        LegalSection(
            heading = "六、知识产权",
            paragraphs = listOf(
                "本产品界面、文案、整理方案的结构与呈现方式等，除另有说明外，归运营方或权利人所有。",
                "游戏名称、图标、截图等知识产权归相应权利人所有，本产品中的引用仅为说明与参考目的。",
            ),
        ),
        LegalSection(
            heading = "七、协议变更与终止",
            paragraphs = listOf(
                "运营方可适时修订本协议，修订后将通过应用内适当位置公布。若您继续使用本产品，即视为接受修订后的协议。",
                "若您不同意本协议，请停止使用本产品。",
            ),
        ),
        LegalSection(
            heading = "八、联系我们",
            paragraphs = listOf(
                "如有疑问，请通过应用「我的 - 意见反馈」联系我们，反馈类型可选择「激活/购卡问题」。",
                "公众号：画质怪兽（hzgsapp）",
            ),
        ),
    )

    private val privacyPolicy = listOf(
        LegalSection(
            heading = "一、我们收集的信息",
            paragraphs = listOf(
                "为提供基础服务，我们可能处理以下信息：",
                "· 设备信息：机型、系统版本等，用于匹配参考方案（主要在本机处理）。",
                "· 设备标识：用于激活状态校验的匿名设备 ID（存储于本机并可能用于服务端查询）。",
                "· 您主动填写的内容：昵称、头像（默认仅存本机）、意见反馈文本及选填联系方式。",
                "· 使用记录：您在本应用内保存的方案、应用历史等（默认存于本机）。",
            ),
        ),
        LegalSection(
            heading = "二、信息的使用",
            paragraphs = listOf(
                "我们使用上述信息用于：提供方案查询与推荐、激活状态验证、改进产品体验、处理您的反馈与售后。",
                "未经您同意，我们不会向第三方出售您的个人信息。",
            ),
        ),
        LegalSection(
            heading = "三、信息的存储与安全",
            paragraphs = listOf(
                "部分数据默认保存在您的设备本地。涉及激活校验的信息可能传输至我们的服务器。",
                "我们将采取合理措施保护信息安全，但无法保证绝对安全。请您妥善保管设备及内容访问码。",
            ),
        ),
        LegalSection(
            heading = "四、您的权利",
            paragraphs = listOf(
                "您可以在「个人资料」中修改或清除本机保存的昵称与头像。",
                "您可以通过意见反馈要求我们协助处理与激活、购卡相关的问题。",
                "若您停止使用本产品，可清除应用缓存或卸载应用以移除本地数据。",
            ),
        ),
        LegalSection(
            heading = "五、未成年人保护",
            paragraphs = listOf(
                "若您为未成年人，请在监护人指导下阅读本政策并使用本产品。监护人应指导未成年人谨慎进行任何付费行为。",
            ),
        ),
        LegalSection(
            heading = "六、政策更新",
            paragraphs = listOf(
                "我们可能适时更新本政策，更新后将通过应用内适当位置公布。",
            ),
        ),
    )

    private val paymentTerms = listOf(
        LegalSection(
            heading = "一、您购买的是什么",
            paragraphs = listOf(
                "通过内容访问码（卡密/激活码）购买的是「画质怪兽应用内参考内容的访问权限」，属于数字化虚拟服务。",
                "该权限不包括：游戏官方会员、游戏内道具、自动改帧软件、硬件性能提升或任何游戏内自动操作功能。",
            ),
        ),
        LegalSection(
            heading = "二、免费与付费的区别",
            paragraphs = listOf(
                "您可通过完成指定任务免费开通内容访问权限。",
                "购买内容访问码适用于希望跳过任务、直接开通的用户。免费与付费开通的内容访问范围以产品内实际展示为准。",
            ),
        ),
        LegalSection(
            heading = "三、购买与激活流程",
            paragraphs = listOf(
                "内容访问码可能通过第三方网页购买（复制链接后在浏览器打开）。请认准官方渠道，谨防诈骗。",
                "购买后，请在本应用「卡密激活」页面输入访问码。每张访问码通常仅可使用一次，绑定设备后请妥善保管。",
                "若激活失败，请检查访问码是否输入正确、是否已被使用，并通过「意见反馈 - 激活/购卡问题」联系我们。",
            ),
        ),
        LegalSection(
            heading = "四、退款与争议",
            paragraphs = listOf(
                "数字化访问码一经成功激活，通常不支持退款，法律法规另有规定或双方另有约定的除外。",
                "未激活且符合购买页政策的，按购买页规则处理退款。",
                "若因系统故障导致重复扣费或未到账访问码，请保留购买凭证并通过意见反馈联系我们，我们将核实后协助处理。",
                "请理性消费。本产品不承诺游戏内效果，因对服务性质理解偏差导致的争议，请结合购买前提示与本文说明协商解决。",
            ),
        ),
        LegalSection(
            heading = "五、售后联系",
            paragraphs = listOf(
                "路径：我的 → 意见反馈 → 选择「激活/购卡问题」。",
                "请尽量提供：购买时间、访问码后四位（勿公开完整访问码）、问题描述与截图。",
                "公众号：画质怪兽（hzgsapp）",
            ),
        ),
        LegalSection(
            heading = "六、服务边界与常见误解",
            paragraphs = listOf(
                "本应用不具备修改游戏客户端、注入、Hook、破解、云改帧等能力。",
                "悬浮窗、启动提示中展示的帧率、画质档位等，均为用户选择的参考方案或设备信息，不代表游戏内已生效。",
                "通过系统 Intent 等方式启动游戏仅为便捷入口，不改变游戏内任何参数。",
                "用户不得将本产品理解为外挂、作弊工具或游戏官方合作产品。",
            ),
        ),
    )
}
