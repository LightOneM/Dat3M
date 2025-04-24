; ModuleID = 'test_cast.c'
source_filename = "test_cast.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-i128:128-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str = private unnamed_addr constant [18 x i8] c"(int*)int_a == &a\00", align 1
@.str.1 = private unnamed_addr constant [12 x i8] c"test_cast.c\00", align 1
@__PRETTY_FUNCTION__.main = private unnamed_addr constant [11 x i8] c"int main()\00", align 1
@.str.2 = private unnamed_addr constant [18 x i8] c"(int*)int_b == &b\00", align 1
@.str.3 = private unnamed_addr constant [19 x i8] c"(char*)int_c == &c\00", align 1
@.str.4 = private unnamed_addr constant [30 x i8] c"((int*)int_a) + 1 == (&a + 1)\00", align 1
@.str.5 = private unnamed_addr constant [30 x i8] c"((int*)int_b) - 1 == (&b - 1)\00", align 1

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
  %8 = ptrtoint ptr %2 to i64
  store i64 %8, ptr %5, align 8
  %9 = ptrtoint ptr %3 to i64
  store i64 %9, ptr %6, align 8
  %10 = ptrtoint ptr %4 to i64
  store i64 %10, ptr %7, align 8
  %11 = load i64, ptr %5, align 8
  %12 = inttoptr i64 %11 to ptr
  %13 = icmp eq ptr %12, %2
  br i1 %13, label %14, label %15

14:                                               ; preds = %0
  br label %16

15:                                               ; preds = %0
  call void @__assert_fail(ptr noundef @.str, ptr noundef @.str.1, i32 noundef 13, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

16:                                               ; preds = %14
  %17 = load i64, ptr %6, align 8
  %18 = inttoptr i64 %17 to ptr
  %19 = icmp eq ptr %18, %3
  br i1 %19, label %20, label %21

20:                                               ; preds = %16
  br label %22

21:                                               ; preds = %16
  call void @__assert_fail(ptr noundef @.str.2, ptr noundef @.str.1, i32 noundef 14, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

22:                                               ; preds = %20
  %23 = load i64, ptr %7, align 8
  %24 = inttoptr i64 %23 to ptr
  %25 = icmp eq ptr %24, %4
  br i1 %25, label %26, label %27

26:                                               ; preds = %22
  br label %28

27:                                               ; preds = %22
  call void @__assert_fail(ptr noundef @.str.3, ptr noundef @.str.1, i32 noundef 15, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

28:                                               ; preds = %26
  %29 = load i64, ptr %5, align 8
  %30 = inttoptr i64 %29 to ptr
  %31 = getelementptr inbounds i32, ptr %30, i64 1
  %32 = getelementptr inbounds i32, ptr %2, i64 1
  %33 = icmp eq ptr %31, %32
  br i1 %33, label %34, label %35

34:                                               ; preds = %28
  br label %36

35:                                               ; preds = %28
  call void @__assert_fail(ptr noundef @.str.4, ptr noundef @.str.1, i32 noundef 17, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

36:                                               ; preds = %34
  %37 = load i64, ptr %6, align 8
  %38 = inttoptr i64 %37 to ptr
  %39 = getelementptr inbounds i32, ptr %38, i64 -1
  %40 = getelementptr inbounds i32, ptr %3, i64 -1
  %41 = icmp eq ptr %39, %40
  br i1 %41, label %42, label %43

42:                                               ; preds = %36
  br label %44

43:                                               ; preds = %36
  call void @__assert_fail(ptr noundef @.str.5, ptr noundef @.str.1, i32 noundef 18, ptr noundef @__PRETTY_FUNCTION__.main) #2
  unreachable

44:                                               ; preds = %42
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
