package com.example.kuiklymmkv

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.utils.urlParams
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.reactive.handler.*
import com.example.kuiklymmkv.base.BasePager
import com.example.kuiklymmkv.base.bridgeModule

@Page("router", supportInLocal = true)
internal class RouterPage : BasePager() {

    var inputText: String = ""
    lateinit var inputRef: ViewRef<InputView>

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                backgroundColor(Color.WHITE)
            }
            // 导航栏
            RouterNavBar {
                attr {
                    title = TITLE
                    backDisable = true
                }
            }

            View {
                attr {
                    allCenter()
                    margin(20f)
                }
                View {
                    attr {
                        backgroundColor(Color.WHITE)
                        borderRadius(10f)
                        padding(10f)
                    }
                    Image {
                        attr {
                            src(LOGO)
                            size(
                                pagerData.pageViewWidth * 0.6f,
                                (pagerData.pageViewWidth * 0.6f) * (1987f / 2894f)
                            )
                        }
                    }
                }
            }

            View {
                attr {
                    flexDirectionRow()
                }
                View {
                    attr {
                        margin(all = 10f)
                        marginTop(0f)
                        height(40f)
                        flex(1f)
                        borderRadius(5f)
                    }
                    View {
                        attr {
                            absolutePositionAllZero()
                            backgroundLinearGradient(
                                Direction.TO_LEFT,
                                ColorStop(Color(0xFF6200EE), 0f),
                                ColorStop(Color(0xFF03DAC5), 1f)
                            )
                        }
                        View {
                            attr {
                                absolutePosition(top = 1f, left = 1f, right = 1f, bottom = 1f)
                                backgroundColor(Color.WHITE)
                                borderRadius(5f)
                            }
                        }
                    }
                    Input {
                        ref {
                            ctx.inputRef = it
                        }
                        attr {
                            flex(1f)
                            fontSize(15f)
                            color(Color(0xFF6200EE))
                            marginLeft(10f)
                            marginRight(10f)
                            placeholder(PLACEHOLDER)
                            autofocus(false)
                            placeholderColor(Color(0xAA03DAC5))
                        }
                        event {
                            textDidChange {
                                ctx.inputText = it.text
                            }
                        }
                    }
                }
                Button {
                    attr {
                        size(80f, 40f)
                        borderRadius(20f)
                        marginLeft(2f)
                        marginRight(15f)
                        backgroundLinearGradient(
                            Direction.TO_BOTTOM,
                            ColorStop(Color(0xAA6200EE), 0f),
                            ColorStop(Color(0xAA03DAC5), 1f)
                        )
                        titleAttr {
                            text(JUMP_TEXT)
                            fontSize(17f)
                            color(Color.WHITE)
                        }
                    }
                    event {
                        click {
                            if (ctx.inputText.isEmpty()) {
                                ctx.bridgeModule.toast("请输入PageName")
                            } else {
                                ctx.inputRef.view?.blur()
                                getPager().acquireModule<SharedPreferencesModule>(
                                    SharedPreferencesModule.MODULE_NAME
                                ).setItem(CACHE_KEY, ctx.inputText)
                                ctx.jumpPage(ctx.inputText)
                            }
                        }
                    }
                }
            }

            Text {
                attr {
                    fontSize(15f)
                    marginLeft(10f)
                    marginTop(5f)
                    text(TIP)
                    backgroundLinearGradient(
                        Direction.TO_RIGHT,
                        ColorStop(Color(0xFF6200EE), 0f),
                        ColorStop(Color(0xFF03DAC5), 1f)
                    )
                }
            }

            // MMKV Demo 入口
            View {
                attr {
                    allCenter()
                    margin(20f)
                }
                View {
                    attr {
                        paddingLeft(24f)
                        paddingRight(24f)
                        paddingTop(12f)
                        paddingBottom(12f)
                        borderRadius(25f)
                        backgroundLinearGradient(
                            Direction.TO_RIGHT,
                            ColorStop(Color(0xFF6200EE), 0f),
                            ColorStop(Color(0xFF03DAC5), 1f)
                        )
                    }
                    Text {
                        attr {
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                            text("🚀 MMKV Demo 演示")
                        }
                    }
                    event {
                        click {
                            ctx.jumpPage("mmkv_demo")
                        }
                    }
                }
            }
        }
    }

    override fun created() {
        super.created()
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        val cacheInputText =
            acquireModule<SharedPreferencesModule>(SharedPreferencesModule.MODULE_NAME).getItem(
                CACHE_KEY
            )
        if (cacheInputText.isNotEmpty()) {
            inputRef.view?.setText(cacheInputText)
        }
    }

    private fun jumpPage(inputText: String) {
        val params = urlParams("pageName=$inputText")
        val pageData = JSONObject()
        params.forEach {
            pageData.put(it.key, it.value)
        }
        val pageName = pageData.optString("pageName")
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).openPage(pageName, pageData)
    }

    companion object {
        const val PLACEHOLDER = "输入pageName"
        const val TIP = "输入规则：router 或者 router&key=value (&后面为页面参数)"
        const val CACHE_KEY = "router_last_input_key2"
        const val LOGO = "https://vfiles.gtimg.cn/wuji_dashboard/wupload/xy/starter/62394e19.png"
        const val JUMP_TEXT = "跳转"
        const val TITLE = "KuiklyMMKV"
    }
}

internal class RouterNavigationBar : ComposeView<RouterNavigationBarAttr, ComposeEvent>() {
    override fun createEvent(): ComposeEvent {
        return ComposeEvent()
    }

    override fun createAttr(): RouterNavigationBarAttr {
        return RouterNavigationBarAttr()
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    paddingTop(ctx.pagerData.statusBarHeight)
                    backgroundColor(Color(0xFF6200EE))
                }
                View {
                    attr {
                        height(44f)
                        allCenter()
                    }
                    Text {
                        attr {
                            text(ctx.attr.title)
                            fontSize(17f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                }
            }
        }
    }
}

internal class RouterNavigationBarAttr : ComposeAttr() {
    var title: String by observable("")
    var backDisable = false
}

internal fun ViewContainer<*, *>.RouterNavBar(init: RouterNavigationBar.() -> Unit) {
    addChild(RouterNavigationBar(), init)
}
