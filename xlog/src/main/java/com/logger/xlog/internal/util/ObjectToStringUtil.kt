package com.logger.xlog.internal.util

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.lang.reflect.InvocationTargetException

/**
 * Utility for formatting object to string.
 */
object ObjectToStringUtil {
    /**
     * Bundle object to string, the string would be in the format of "Bundle[{...}]".
     */
    fun bundleToString(bundle: Bundle?): String {
        if (bundle == null) {
            return "null"
        }

        val b = StringBuilder(128)
        b.append("Bundle[{")
        bundleToShortString(bundle, b)
        b.append("}]")
        return b.toString()
    }

    /**
     * Intent object to string, the string would be in the format of "Intent { ... }".
     */
    fun intentToString(intent: Intent?): String {
        if (intent == null) {
            return "null"
        }

        val b = StringBuilder(128)
        b.append("Intent { ")
        intentToShortString(intent, b)
        b.append(" }")
        return b.toString()
    }

    @Suppress("DEPRECATION")
    private fun bundleToShortString(bundle: Bundle, b: StringBuilder) {
        var first = true
        for (key in bundle.keySet()) {
            if (!first) {
                b.append(", ")
            }
            b.append(key).append('=')
            val value = bundle[key]
            if (value is IntArray) {
                b.append(value.contentToString())
            } else if (value is ByteArray) {
                b.append(value.contentToString())
            } else if (value is BooleanArray) {
                b.append(value.contentToString())
            } else if (value is ShortArray) {
                b.append(value.contentToString())
            } else if (value is LongArray) {
                b.append(value.contentToString())
            } else if (value is FloatArray) {
                b.append(value.contentToString())
            } else if (value is DoubleArray) {
                b.append(value.contentToString())
            } else if (value is Array<*> && value.isArrayOf<String>()) {
                b.append(value.contentToString())
            } else if (value is Array<*> && value.isArrayOf<CharSequence>()) {
                b.append(value.contentToString())
            } else if (value is Array<*> && value.isArrayOf<Parcelable>()) {
                b.append(value.contentToString())
            } else if (value is Bundle) {
                b.append(bundleToString(value))
            } else {
                b.append(value)
            }
            first = false
        }
    }

    private fun intentToShortString(intent: Intent, b: StringBuilder) {
        var first = true
        val mAction = intent.action
        if (mAction != null) {
            b.append("act=").append(mAction)
            first = false
        }
        val mCategories = intent.categories
        if (mCategories != null) {
            if (!first) {
                b.append(' ')
            }
            first = false
            b.append("cat=[")
            var firstCategory = true
            for (c in mCategories) {
                if (!firstCategory) {
                    b.append(',')
                }
                b.append(c)
                firstCategory = false
            }
            b.append("]")
        }
        val mData = intent.data
        if (mData != null) {
            if (!first) {
                b.append(' ')
            }
            first = false
            b.append("dat=")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                b.append(uriToSafeString(mData))
            } else {
                val scheme = mData.scheme
                if (scheme != null) {
                    if (scheme.equals("tel", ignoreCase = true)) {
                        b.append("tel:xxx-xxx-xxxx")
                    } else if (scheme.equals("smsto", ignoreCase = true)) {
                        b.append("smsto:xxx-xxx-xxxx")
                    } else {
                        b.append(mData)
                    }
                } else {
                    b.append(mData)
                }
            }
        }
        val mType = intent.type
        if (mType != null) {
            if (!first) {
                b.append(' ')
            }
            first = false
            b.append("typ=").append(mType)
        }
        val mFlags = intent.flags
        if (mFlags != 0) {
            if (!first) {
                b.append(' ')
            }
            first = false
            b.append("flg=0x").append(Integer.toHexString(mFlags))
        }
        val mPackage = intent.getPackage()
        if (mPackage != null) {
            if (!first) {
                b.append(' ')
            }
            first = false
            b.append("pkg=").append(mPackage)
        }
        val mComponent = intent.component
        if (mComponent != null) {
            if (!first) {
                b.append(' ')
            }
            first = false
            b.append("cmp=").append(mComponent.flattenToShortString())
        }
        val mSourceBounds = intent.sourceBounds
        if (mSourceBounds != null) {
            if (!first) {
                b.append(' ')
            }
            first = false
            b.append("bnds=").append(mSourceBounds.toShortString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val mClipData = intent.clipData
            if (mClipData != null) {
                if (!first) {
                    b.append(' ')
                }
                first = false
                b.append("(has clip)")
            }
        }
        val mExtras = intent.extras
        if (mExtras != null) {
            if (!first) {
                b.append(' ')
            }
            b.append("extras={")
            bundleToShortString(mExtras, b)
            b.append('}')
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            val mSelector = intent.selector
            if (mSelector != null) {
                b.append(" sel=")
                intentToShortString(mSelector, b)
                b.append("}")
            }
        }
    }

    private fun uriToSafeString(uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                val toSafeString = Uri::class.java.getDeclaredMethod("toSafeString")
                toSafeString.isAccessible = true
                return toSafeString.invoke(uri) as String
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return uri.toString()
    }
}
