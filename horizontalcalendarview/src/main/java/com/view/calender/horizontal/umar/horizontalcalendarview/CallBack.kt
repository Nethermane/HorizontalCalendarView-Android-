package com.view.calender.horizontal.umar.horizontalcalendarview

import java.lang.reflect.InvocationTargetException

/**
 * Created by UManzoor on 2/8/2018.
 */

class CallBack/*
        Sending Third Parameter. For Internet Check.
     */
(private val scope: HorizontalCalendarListener, private val methodName: String) {

    @Throws(InvocationTargetException::class, IllegalAccessException::class, NoSuchMethodException::class)
    operator fun invoke(vararg parameters: Any): Any? {
        val method = scope.javaClass.getMethod(methodName, *getParameterClasses(*parameters))
        return method.invoke(scope, *parameters)
    }

    private fun getParameterClasses(vararg parameters: Any): Array<Class<*>> {
        val classes = Array<Class<*>>(parameters.size) {Int::class.java}
        for (i in classes.indices) {
            classes[i] = parameters[i].javaClass
        }
        return classes
    }


}
