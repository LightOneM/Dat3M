; ModuleID = 'test_compr.c'
source_filename = "test_compr.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-i128:128-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@__const.main.arr = private unnamed_addr constant [5 x i32] [i32 10, i32 20, i32 30, i32 40, i32 50], align 16
@.str = private unnamed_addr constant [15 x i8] c"*(p + 1) == 20\00", align 1
@.str.1 = private unnamed_addr constant [13 x i8] c"test_compr.c\00", align 1
@__PRETTY_FUNCTION__.main = private unnamed_addr constant [11 x i8] c"int main()\00", align 1
@.str.2 = private unnamed_addr constant [15 x i8] c"*(p + 1) != 50\00", align 1
@.str.3 = private unnamed_addr constant [15 x i8] c"*(p + 4) == 50\00", align 1
@.str.4 = private unnamed_addr constant [15 x i8] c"*(p + 4) != 20\00", align 1
@.str.5 = private unnamed_addr constant [17 x i8] c"end - start == 4\00", align 1
@.str.6 = private unnamed_addr constant [17 x i8] c"end - start != 5\00", align 1
@.str.7 = private unnamed_addr constant [19 x i8] c"*(p + i) == arr[i]\00", align 1
@.str.8 = private unnamed_addr constant [23 x i8] c"*(p + i + 1) != arr[i]\00", align 1
@.str.9 = private unnamed_addr constant [24 x i8] c"*(intPtr + i) == arr[i]\00", align 1
@.str.10 = private unnamed_addr constant [28 x i8] c"*(intPtr + i + 1) != arr[i]\00", align 1
@.str.11 = private unnamed_addr constant [13 x i8] c"arr[2] == 35\00", align 1
@.str.12 = private unnamed_addr constant [13 x i8] c"arr[2] != 20\00", align 1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca [5 x i32], align 16
  %3 = alloca ptr, align 8
  %4 = alloca ptr, align 8
  %5 = alloca ptr, align 8
  %6 = alloca i32, align 4
  %7 = alloca ptr, align 8
  %8 = alloca ptr, align 8
  %9 = alloca i32, align 4
  store i32 0, ptr %1, align 4
  call void @llvm.memcpy.p0.p0.i64(ptr align 16 %2, ptr align 16 @__const.main.arr, i64 20, i1 false)
  %10 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 0
  store ptr %10, ptr %3, align 8
  %11 = load ptr, ptr %3, align 8
  %12 = getelementptr inbounds i32, ptr %11, i64 1
  %13 = load i32, ptr %12, align 4
  %14 = icmp eq i32 %13, 20
  br i1 %14, label %15, label %16

15:                                               ; preds = %0
  br label %17

16:                                               ; preds = %0
  call void @__assert_fail(ptr noundef @.str, ptr noundef @.str.1, i32 noundef 7, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

17:                                               ; preds = %15
  %18 = load ptr, ptr %3, align 8
  %19 = getelementptr inbounds i32, ptr %18, i64 1
  %20 = load i32, ptr %19, align 4
  %21 = icmp ne i32 %20, 50
  br i1 %21, label %22, label %23

22:                                               ; preds = %17
  br label %24

23:                                               ; preds = %17
  call void @__assert_fail(ptr noundef @.str.2, ptr noundef @.str.1, i32 noundef 8, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

24:                                               ; preds = %22
  %25 = load ptr, ptr %3, align 8
  %26 = getelementptr inbounds i32, ptr %25, i64 4
  %27 = load i32, ptr %26, align 4
  %28 = icmp eq i32 %27, 50
  br i1 %28, label %29, label %30

29:                                               ; preds = %24
  br label %31

30:                                               ; preds = %24
  call void @__assert_fail(ptr noundef @.str.3, ptr noundef @.str.1, i32 noundef 9, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

31:                                               ; preds = %29
  %32 = load ptr, ptr %3, align 8
  %33 = getelementptr inbounds i32, ptr %32, i64 4
  %34 = load i32, ptr %33, align 4
  %35 = icmp ne i32 %34, 20
  br i1 %35, label %36, label %37

36:                                               ; preds = %31
  br label %38

37:                                               ; preds = %31
  call void @__assert_fail(ptr noundef @.str.4, ptr noundef @.str.1, i32 noundef 10, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

38:                                               ; preds = %36
  %39 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 0
  store ptr %39, ptr %4, align 8
  %40 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 4
  store ptr %40, ptr %5, align 8
  %41 = load ptr, ptr %5, align 8
  %42 = load ptr, ptr %4, align 8
  %43 = ptrtoint ptr %41 to i64
  %44 = ptrtoint ptr %42 to i64
  %45 = sub i64 %43, %44
  %46 = sdiv exact i64 %45, 4
  %47 = icmp eq i64 %46, 4
  br i1 %47, label %48, label %49

48:                                               ; preds = %38
  br label %50

49:                                               ; preds = %38
  call void @__assert_fail(ptr noundef @.str.5, ptr noundef @.str.1, i32 noundef 13, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

50:                                               ; preds = %48
  %51 = load ptr, ptr %5, align 8
  %52 = load ptr, ptr %4, align 8
  %53 = ptrtoint ptr %51 to i64
  %54 = ptrtoint ptr %52 to i64
  %55 = sub i64 %53, %54
  %56 = sdiv exact i64 %55, 4
  %57 = icmp ne i64 %56, 5
  br i1 %57, label %58, label %59

58:                                               ; preds = %50
  br label %60

59:                                               ; preds = %50
  call void @__assert_fail(ptr noundef @.str.6, ptr noundef @.str.1, i32 noundef 14, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

60:                                               ; preds = %58
  store i32 0, ptr %6, align 4
  br label %61

61:                                               ; preds = %92, %60
  %62 = load i32, ptr %6, align 4
  %63 = icmp slt i32 %62, 5
  br i1 %63, label %64, label %95

64:                                               ; preds = %61
  %65 = load ptr, ptr %3, align 8
  %66 = load i32, ptr %6, align 4
  %67 = sext i32 %66 to i64
  %68 = getelementptr inbounds i32, ptr %65, i64 %67
  %69 = load i32, ptr %68, align 4
  %70 = load i32, ptr %6, align 4
  %71 = sext i32 %70 to i64
  %72 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 %71
  %73 = load i32, ptr %72, align 4
  %74 = icmp eq i32 %69, %73
  br i1 %74, label %75, label %76

75:                                               ; preds = %64
  br label %77

76:                                               ; preds = %64
  call void @__assert_fail(ptr noundef @.str.7, ptr noundef @.str.1, i32 noundef 16, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

77:                                               ; preds = %75
  %78 = load ptr, ptr %3, align 8
  %79 = load i32, ptr %6, align 4
  %80 = sext i32 %79 to i64
  %81 = getelementptr inbounds i32, ptr %78, i64 %80
  %82 = getelementptr inbounds i32, ptr %81, i64 1
  %83 = load i32, ptr %82, align 4
  %84 = load i32, ptr %6, align 4
  %85 = sext i32 %84 to i64
  %86 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 %85
  %87 = load i32, ptr %86, align 4
  %88 = icmp ne i32 %83, %87
  br i1 %88, label %89, label %90

89:                                               ; preds = %77
  br label %91

90:                                               ; preds = %77
  call void @__assert_fail(ptr noundef @.str.8, ptr noundef @.str.1, i32 noundef 17, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

91:                                               ; preds = %89
  br label %92

92:                                               ; preds = %91
  %93 = load i32, ptr %6, align 4
  %94 = add nsw i32 %93, 1
  store i32 %94, ptr %6, align 4
  br label %61, !llvm.loop !6

95:                                               ; preds = %61
  %96 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 0
  store ptr %96, ptr %7, align 8
  %97 = load ptr, ptr %7, align 8
  store ptr %97, ptr %8, align 8
  store i32 0, ptr %9, align 4
  br label %98

98:                                               ; preds = %129, %95
  %99 = load i32, ptr %9, align 4
  %100 = icmp slt i32 %99, 5
  br i1 %100, label %101, label %132

101:                                              ; preds = %98
  %102 = load ptr, ptr %8, align 8
  %103 = load i32, ptr %9, align 4
  %104 = sext i32 %103 to i64
  %105 = getelementptr inbounds i32, ptr %102, i64 %104
  %106 = load i32, ptr %105, align 4
  %107 = load i32, ptr %9, align 4
  %108 = sext i32 %107 to i64
  %109 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 %108
  %110 = load i32, ptr %109, align 4
  %111 = icmp eq i32 %106, %110
  br i1 %111, label %112, label %113

112:                                              ; preds = %101
  br label %114

113:                                              ; preds = %101
  call void @__assert_fail(ptr noundef @.str.9, ptr noundef @.str.1, i32 noundef 22, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

114:                                              ; preds = %112
  %115 = load ptr, ptr %8, align 8
  %116 = load i32, ptr %9, align 4
  %117 = sext i32 %116 to i64
  %118 = getelementptr inbounds i32, ptr %115, i64 %117
  %119 = getelementptr inbounds i32, ptr %118, i64 1
  %120 = load i32, ptr %119, align 4
  %121 = load i32, ptr %9, align 4
  %122 = sext i32 %121 to i64
  %123 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 %122
  %124 = load i32, ptr %123, align 4
  %125 = icmp ne i32 %120, %124
  br i1 %125, label %126, label %127

126:                                              ; preds = %114
  br label %128

127:                                              ; preds = %114
  call void @__assert_fail(ptr noundef @.str.10, ptr noundef @.str.1, i32 noundef 23, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

128:                                              ; preds = %126
  br label %129

129:                                              ; preds = %128
  %130 = load i32, ptr %9, align 4
  %131 = add nsw i32 %130, 1
  store i32 %131, ptr %9, align 4
  br label %98, !llvm.loop !8

132:                                              ; preds = %98
  %133 = load ptr, ptr %3, align 8
  %134 = getelementptr inbounds i32, ptr %133, i64 2
  store i32 35, ptr %134, align 4
  %135 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 2
  %136 = load i32, ptr %135, align 8
  %137 = icmp eq i32 %136, 35
  br i1 %137, label %138, label %139

138:                                              ; preds = %132
  br label %140

139:                                              ; preds = %132
  call void @__assert_fail(ptr noundef @.str.11, ptr noundef @.str.1, i32 noundef 26, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

140:                                              ; preds = %138
  %141 = getelementptr inbounds [5 x i32], ptr %2, i64 0, i64 2
  %142 = load i32, ptr %141, align 8
  %143 = icmp ne i32 %142, 20
  br i1 %143, label %144, label %145

144:                                              ; preds = %140
  br label %146

145:                                              ; preds = %140
  call void @__assert_fail(ptr noundef @.str.12, ptr noundef @.str.1, i32 noundef 27, ptr noundef @__PRETTY_FUNCTION__.main) #3
  unreachable

146:                                              ; preds = %144
  ret i32 0
}

; Function Attrs: nocallback nofree nounwind willreturn memory(argmem: readwrite)
declare void @llvm.memcpy.p0.p0.i64(ptr noalias nocapture writeonly, ptr noalias nocapture readonly, i64, i1 immarg) #1

; Function Attrs: noreturn nounwind
declare void @__assert_fail(ptr noundef, ptr noundef, i32 noundef, ptr noundef) #2

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { nocallback nofree nounwind willreturn memory(argmem: readwrite) }
attributes #2 = { noreturn nounwind "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #3 = { noreturn nounwind }

!llvm.module.flags = !{!0, !1, !2, !3, !4}
!llvm.ident = !{!5}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{i32 8, !"PIC Level", i32 2}
!2 = !{i32 7, !"PIE Level", i32 2}
!3 = !{i32 7, !"uwtable", i32 2}
!4 = !{i32 7, !"frame-pointer", i32 2}
!5 = !{!"Ubuntu clang version 18.1.3 (1ubuntu1)"}
!6 = distinct !{!6, !7}
!7 = !{!"llvm.loop.mustprogress"}
!8 = distinct !{!8, !7}
