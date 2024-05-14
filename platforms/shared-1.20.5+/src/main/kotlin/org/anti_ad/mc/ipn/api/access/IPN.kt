/*
 * MIT License
 *
 * Copyright (c) 2022 Plamen K. Kosseff
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.anti_ad.mc.ipn.api.access

abstract class IPN {

    abstract val containerClicker: IContainerClicker

    abstract val lockedSlots: List<Int>

    companion object {
        @JvmStatic
        protected var _IPN: IPN? = null
            @JvmStatic
            set

        @JvmStatic
        val instance: IPN
            @JvmStatic
            get()  {
                return _IPN ?: DummyIPN()
            }

        private class DummyIPN: IPN() {

            override val containerClicker: IContainerClicker
                get() = TODO("Not yet implemented")

            override val lockedSlots: List<Int>
                get() = TODO("Not yet implemented")
        }
    }

}
