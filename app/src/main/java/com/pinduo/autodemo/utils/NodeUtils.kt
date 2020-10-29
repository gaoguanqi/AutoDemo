package com.pinduo.autodemo.utils

import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo

class NodeUtils {
    companion object{

        fun onClickByNode(node:AccessibilityNodeInfo?):Boolean{
            node?.let {
                if(it.isClickable){
                    return it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }else{
                    val parent = it.parent
                    val b = onClickByNode(parent)
                    parent?.recycle()
                    return b
                }
            }
            return false
        }


        fun onClickTextByNode(node:AccessibilityNodeInfo?){
            node?.let {
                for (i in 0 until it.childCount) {
                    if (TextUtils.equals(
                            "android.widget.TextView",
                            it.getChild(i).className
                        ) && !TextUtils.isEmpty(it.getChild(i).text)
                    ){
                        onClickByNode(it.getChild(i))
                    }else{
                        onClickTextByNode(it.getChild(i))
                    }
                }
            }
        }

    }
}