; ModuleID = '/home/lightone/Desktop/Dat3M/benchmarks/ptr/oob.c'
source_filename = "/home/lightone/Desktop/Dat3M/benchmarks/ptr/oob.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-i128:128-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

%struct.Data = type { [2 x i32], [5 x i8] }

@int_array = dso_local global [3 x i32] [i32 1, i32 2, i32 3], align 4
@struct_array = dso_local global [3 x %struct.Data] [%struct.Data { [2 x i32] [i32 1, i32 2], [5 x i8] c"a\00\00\00\00" }, %struct.Data { [2 x i32] [i32 3, i32 4], [5 x i8] c"b\00\00\00\00" }, %struct.Data { [2 x i32] [i32 5, i32 6], [5 x i8] c"c\00\00\00\00" }], align 16

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca ptr, align 8
  %5 = alloca i32, align 4
  %6 = alloca ptr, align 8
  %7 = alloca i8, align 1
  store i32 0, ptr %1, align 4
  %8 = load i32, ptr getelementptr inbounds ([3 x i32], ptr @int_array, i64 1, i64 1), align 4
  store i32 %8, ptr %2, align 4
  %9 = load i32, ptr getelementptr inbounds ([3 x %struct.Data], ptr @struct_array, i64 1, i64 0), align 16
  store i32 %9, ptr %3, align 4
  store ptr getelementptr inbounds ([3 x %struct.Data], ptr @struct_array, i64 0, i64 1), ptr %4, align 8
  %10 = load ptr, ptr %4, align 8
  %11 = getelementptr inbounds i32, ptr %10, i64 3
  %12 = load i32, ptr %11, align 4
  store i32 %12, ptr %5, align 4
  store ptr getelementptr inbounds ([3 x %struct.Data], ptr @struct_array, i64 0, i64 2, i32 1), ptr %6, align 8
  %13 = load ptr, ptr %6, align 8
  %14 = getelementptr inbounds i8, ptr %13, i64 6
  %15 = load i8, ptr %14, align 1
  store i8 %15, ptr %7, align 1
  ret i32 0
}

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }

!llvm.module.flags = !{!0, !1, !2, !3, !4}
!llvm.ident = !{!5}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{i32 8, !"PIC Level", i32 2}
!2 = !{i32 7, !"PIE Level", i32 2}
!3 = !{i32 7, !"uwtable", i32 2}
!4 = !{i32 7, !"frame-pointer", i32 2}
!5 = !{!"Ubuntu clang version 18.1.3 (1ubuntu1)"}
