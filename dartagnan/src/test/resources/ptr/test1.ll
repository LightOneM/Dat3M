; ModuleID = 'test.c'
source_filename = "test.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-i128:128-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str = private unnamed_addr constant [6 x i8] c"b > a\00", align 1
@.str.1 = private unnamed_addr constant [7 x i8] c"test.c\00", align 1
@__PRETTY_FUNCTION__.main = private unnamed_addr constant [11 x i8] c"int main()\00", align 1
@.str.2 = private unnamed_addr constant [24 x i8] c"(int*)int_a == (int*)&a\00", align 1
@.str.3 = private unnamed_addr constant [24 x i8] c"(int*)int_b == (int*)&b\00", align 1
@.str.4 = private unnamed_addr constant [24 x i8] c"(int*)int_c == (int*)&c\00", align 1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca i8, align 1
  %5 = alloca i64, align 8
  %6 = alloca i64, align 8
  %7 = alloca i64, align 8
  store i32 0, ptr %1, align 4
  store i32 0, ptr %2, align 4
  store i32 1, ptr %3, align 4
  store i8 99, ptr %4, align 1
  %8 = load i32, ptr %3, align 4
  %9 = load i32, ptr %2, align 4
  %10 = icmp sgt i32 %8, %9
  br i1 %10, label %11, label %12

11:                                               ; preds = %0
  br label %13

12:                                               ; preds = %0
  call void @__assert_fail(ptr noundef @.str, ptr noundef @.str.1, i32 noundef 10, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

13:                                               ; preds = %11
  %14 = ptrtoint ptr %2 to i64
  store i64 %14, ptr %5, align 8
  %15 = ptrtoint ptr %3 to i64
  store i64 %15, ptr %6, align 8
  %16 = ptrtoint ptr %4 to i64
  store i64 %16, ptr %7, align 8
  %17 = load i64, ptr %5, align 8
  %18 = inttoptr i64 %17 to ptr
  %19 = icmp eq ptr %18, %2
  br i1 %19, label %20, label %21

20:                                               ; preds = %13
  br label %22

21:                                               ; preds = %13
  call void @__assert_fail(ptr noundef @.str.2, ptr noundef @.str.1, i32 noundef 16, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

22:                                               ; preds = %20
  %23 = load i64, ptr %6, align 8
  %24 = inttoptr i64 %23 to ptr
  %25 = icmp eq ptr %24, %3
  br i1 %25, label %26, label %27

26:                                               ; preds = %22
  br label %28

27:                                               ; preds = %22
  call void @__assert_fail(ptr noundef @.str.3, ptr noundef @.str.1, i32 noundef 17, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

28:                                               ; preds = %26
  %29 = load i64, ptr %7, align 8
  %30 = inttoptr i64 %29 to ptr
  %31 = icmp eq ptr %30, %4
  br i1 %31, label %32, label %33

32:                                               ; preds = %28
  br label %34

33:                                               ; preds = %28
  call void @__assert_fail(ptr noundef @.str.4, ptr noundef @.str.1, i32 noundef 18, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

34:                                               ; preds = %32
  ret i32 0
}

; Function Attrs: noreturn nounwind
declare void @__assert_fail(ptr noundef, ptr noundef, i32 noundef, ptr noundef) #1

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { noreturn nounwind "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #2 = { noreturn nounwind }

!llvm.module.flags = !{!0, !1, !2, !3, !4}
!llvm.ident = !{!5}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{i32 8, !"PIC Level", i32 2}
!2 = !{i32 7, !"PIE Level", i32 2}
!3 = !{i32 7, !"uwtable", i32 2}
!4 = !{i32 7, !"frame-pointer", i32 2}
!5 = !{!"Ubuntu clang version 18.1.3 (1ubuntu1)"}
