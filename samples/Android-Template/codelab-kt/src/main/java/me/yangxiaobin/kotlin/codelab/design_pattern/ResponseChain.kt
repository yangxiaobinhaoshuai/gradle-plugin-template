package me.yangxiaobin.kotlin.codelab.design_pattern



interface Interceptor<ORIGIN, PROCESSED> {

    fun intercept(chain: Chain<ORIGIN, PROCESSED>): PROCESSED

    interface Chain<ORIGIN, PROCESSED> {

        fun original(): ORIGIN

        fun proceed(origin: ORIGIN): PROCESSED
    }
}

class ChainImpl<O, P>(
    private val interceptors: List<Interceptor<O, P>>,
    private val index: Int,
    private val origin: O,
) : Interceptor.Chain<O, P> {

    override fun original(): O = origin

    override fun proceed(origin: O): P {
        val nextInterceptor = interceptors[index]
        val nextChain = ChainImpl(interceptors, index + 1, origin)
        return nextInterceptor.intercept(nextChain)
    }
}

typealias  InterceptorFunc<ORIGIN, PROCESSED> = (chain: Interceptor.Chain<ORIGIN, PROCESSED>) -> PROCESSED


/**
 * Usage :
 *
 *      fun <I,O> foo(args): O {
  *        val handler = ResponseChainHandler<I, O>(input)
  *
  *        handler.add(this::process)
  *        handler.add(this::convertType)
 *         // or
 *         handler.add { chain ->
 *           val origin = chain.origin()
 *           // process with origin
 *           return chain.proceed(origin)
 *         }
  *
  *        return handler.getProcessed()
  *      }
 *
 *       private fun <I,O> process(chain: Interceptor.Chain<I, O>): O {
 *          val originList = chain.original()
 *          // do stuff with originList
 *          return chain.proceed(originList)
 *       }
 *
 *        private fun <I,O> convertType(chain: Interceptor.Chain<I, O>): O {
 *           val origin = chain.original()
 *          // do stuff
 *          return result
 *        }
 *
 */
class ResponseChainHandler<ORIGIN, PROCESSED>(private val origin: ORIGIN) {

    private val interceptors = mutableListOf<Interceptor<ORIGIN, PROCESSED>>()

    operator fun plus(interceptor: Interceptor<ORIGIN, PROCESSED>) = apply { interceptors.add(interceptor) }

    fun add(interceptor: InterceptorFunc<ORIGIN, PROCESSED>) = apply {
        plus(object : Interceptor<ORIGIN, PROCESSED> {
            override fun intercept(chain: Interceptor.Chain<ORIGIN, PROCESSED>): PROCESSED =
                interceptor.invoke(chain)
        })
    }

    fun getProcessed(): PROCESSED = ChainImpl(interceptors, 0, origin).proceed(origin)

}

