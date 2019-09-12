package com.hsicen.lib;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * <p>作者：Hsicen  2019/9/11 15:04
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：InterView
 */
public class NamingCovertDetector extends Detector
        implements Detector.UastScanner {

    public static final Issue ISSUE = Issue.create("NamingConventionWarning",
            "命名规范错误", "使用驼峰命名法，方法命名开头小写",
            Category.USABILITY, 5, Severity.WARNING, new Implementation(NamingCovertDetector.class,
                    EnumSet.of(Scope.valueOf("kt"))));

    @Nullable
    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {

        return Collections.<Class<? extends UElement>>singletonList(UClass.class);
    }


}
