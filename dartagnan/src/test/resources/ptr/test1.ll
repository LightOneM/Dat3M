; ModuleID = 'dglm_test.c'
source_filename = "dglm_test.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-i128:128-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

%struct.Node = type { i32, ptr }

@Head = dso_local global ptr null, align 8
@Tail = dso_local global ptr null, align 8
@.str = private unnamed_addr constant [13 x i8] c"tail != NULL\00", align 1
@.str.1 = private unnamed_addr constant [9 x i8] c"./dglm.h\00", align 1
@__PRETTY_FUNCTION__.enqueue = private unnamed_addr constant [18 x i8] c"void enqueue(int)\00", align 1
@.str.2 = private unnamed_addr constant [13 x i8] c"head != NULL\00", align 1
@__PRETTY_FUNCTION__.dequeue = private unnamed_addr constant [14 x i8] c"int dequeue()\00", align 1
@.str.3 = private unnamed_addr constant [11 x i8] c"r != EMPTY\00", align 1
@.str.4 = private unnamed_addr constant [12 x i8] c"dglm_test.c\00", align 1
@__PRETTY_FUNCTION__.worker = private unnamed_addr constant [21 x i8] c"void *worker(void *)\00", align 1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @init() #0 {
  %1 = alloca ptr, align 8
  %2 = call noalias ptr @malloc(i64 noundef 16) #4
  store ptr %2, ptr %1, align 8
  %3 = load ptr, ptr %1, align 8
  %4 = getelementptr inbounds %struct.Node, ptr %3, i32 0, i32 1
  store ptr null, ptr %4, align 8
  %5 = load ptr, ptr %1, align 8
  store ptr %5, ptr @Head, align 8
  %6 = load ptr, ptr %1, align 8
  store ptr %6, ptr @Tail, align 8
  ret void
}

; Function Attrs: nounwind allocsize(0)
declare noalias ptr @malloc(i64 noundef) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @enqueue(i32 noundef %0) #0 {
  %2 = alloca i32, align 4
  %3 = alloca ptr, align 8
  %4 = alloca ptr, align 8
  %5 = alloca ptr, align 8
  %6 = alloca ptr, align 8
  %7 = alloca ptr, align 8
  %8 = alloca ptr, align 8
  %9 = alloca ptr, align 8
  %10 = alloca i8, align 1
  %11 = alloca ptr, align 8
  %12 = alloca i8, align 1
  %13 = alloca ptr, align 8
  %14 = alloca i8, align 1
  store i32 %0, ptr %2, align 4
  %15 = call noalias ptr @malloc(i64 noundef 16) #4
  store ptr %15, ptr %5, align 8
  %16 = load i32, ptr %2, align 4
  %17 = load ptr, ptr %5, align 8
  %18 = getelementptr inbounds %struct.Node, ptr %17, i32 0, i32 0
  store i32 %16, ptr %18, align 8
  %19 = load ptr, ptr %5, align 8
  %20 = getelementptr inbounds %struct.Node, ptr %19, i32 0, i32 1
  store ptr null, ptr %20, align 8
  br label %21

21:                                               ; preds = %1, %80
  %22 = load atomic i64, ptr @Tail acquire, align 8
  store i64 %22, ptr %6, align 8
  %23 = load ptr, ptr %6, align 8
  store ptr %23, ptr %3, align 8
  %24 = load ptr, ptr %3, align 8
  %25 = icmp ne ptr %24, null
  br i1 %25, label %26, label %27

26:                                               ; preds = %21
  br label %28

27:                                               ; preds = %21
  call void @__assert_fail(ptr noundef @.str, ptr noundef @.str.1, i32 noundef 38, ptr noundef @__PRETTY_FUNCTION__.enqueue) #5
  unreachable

28:                                               ; preds = %26
  %29 = load ptr, ptr %3, align 8
  %30 = getelementptr inbounds %struct.Node, ptr %29, i32 0, i32 1
  %31 = load atomic i64, ptr %30 acquire, align 8
  store i64 %31, ptr %7, align 8
  %32 = load ptr, ptr %7, align 8
  store ptr %32, ptr %4, align 8
  %33 = load ptr, ptr %3, align 8
  %34 = load atomic i64, ptr @Tail acquire, align 8
  store i64 %34, ptr %8, align 8
  %35 = load ptr, ptr %8, align 8
  %36 = icmp eq ptr %33, %35
  br i1 %36, label %37, label %80

37:                                               ; preds = %28
  %38 = load ptr, ptr %4, align 8
  %39 = icmp eq ptr %38, null
  br i1 %39, label %40, label %67

40:                                               ; preds = %37
  %41 = load ptr, ptr %3, align 8
  %42 = getelementptr inbounds %struct.Node, ptr %41, i32 0, i32 1
  %43 = load ptr, ptr %5, align 8
  store ptr %43, ptr %9, align 8
  %44 = load i64, ptr %4, align 8
  %45 = load i64, ptr %9, align 8
  %46 = cmpxchg ptr %42, i64 %44, i64 %45 acq_rel monotonic, align 8
  %47 = extractvalue { i64, i1 } %46, 0
  %48 = extractvalue { i64, i1 } %46, 1
  br i1 %48, label %50, label %49

49:                                               ; preds = %40
  store i64 %47, ptr %4, align 8
  br label %50

50:                                               ; preds = %49, %40
  %51 = zext i1 %48 to i8
  store i8 %51, ptr %10, align 1
  %52 = load i8, ptr %10, align 1
  %53 = trunc i8 %52 to i1
  br i1 %53, label %54, label %66

54:                                               ; preds = %50
  %55 = load ptr, ptr %5, align 8
  store ptr %55, ptr %11, align 8
  %56 = load i64, ptr %3, align 8
  %57 = load i64, ptr %11, align 8
  %58 = cmpxchg ptr @Tail, i64 %56, i64 %57 acq_rel monotonic, align 8
  %59 = extractvalue { i64, i1 } %58, 0
  %60 = extractvalue { i64, i1 } %58, 1
  br i1 %60, label %62, label %61

61:                                               ; preds = %54
  store i64 %59, ptr %3, align 8
  br label %62

62:                                               ; preds = %61, %54
  %63 = zext i1 %60 to i8
  store i8 %63, ptr %12, align 1
  %64 = load i8, ptr %12, align 1
  %65 = trunc i8 %64 to i1
  br label %81

66:                                               ; preds = %50
  br label %79

67:                                               ; preds = %37
  %68 = load ptr, ptr %4, align 8
  store ptr %68, ptr %13, align 8
  %69 = load i64, ptr %3, align 8
  %70 = load i64, ptr %13, align 8
  %71 = cmpxchg ptr @Tail, i64 %69, i64 %70 acq_rel monotonic, align 8
  %72 = extractvalue { i64, i1 } %71, 0
  %73 = extractvalue { i64, i1 } %71, 1
  br i1 %73, label %75, label %74

74:                                               ; preds = %67
  store i64 %72, ptr %3, align 8
  br label %75

75:                                               ; preds = %74, %67
  %76 = zext i1 %73 to i8
  store i8 %76, ptr %14, align 1
  %77 = load i8, ptr %14, align 1
  %78 = trunc i8 %77 to i1
  br label %79

79:                                               ; preds = %75, %66
  br label %80

80:                                               ; preds = %79, %28
  br label %21

81:                                               ; preds = %62
  ret void
}

; Function Attrs: noreturn nounwind
declare void @__assert_fail(ptr noundef, ptr noundef, i32 noundef, ptr noundef) #2

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @dequeue() #0 {
  %1 = alloca ptr, align 8
  %2 = alloca ptr, align 8
  %3 = alloca ptr, align 8
  %4 = alloca i32, align 4
  %5 = alloca ptr, align 8
  %6 = alloca ptr, align 8
  %7 = alloca ptr, align 8
  %8 = alloca ptr, align 8
  %9 = alloca i8, align 1
  %10 = alloca ptr, align 8
  %11 = alloca ptr, align 8
  %12 = alloca i8, align 1
  br label %13

13:                                               ; preds = %0, %74
  %14 = load atomic i64, ptr @Head acquire, align 8
  store i64 %14, ptr %5, align 8
  %15 = load ptr, ptr %5, align 8
  store ptr %15, ptr %1, align 8
  %16 = load ptr, ptr %1, align 8
  %17 = icmp ne ptr %16, null
  br i1 %17, label %18, label %19

18:                                               ; preds = %13
  br label %20

19:                                               ; preds = %13
  call void @__assert_fail(ptr noundef @.str.2, ptr noundef @.str.1, i32 noundef 61, ptr noundef @__PRETTY_FUNCTION__.dequeue) #5
  unreachable

20:                                               ; preds = %18
  %21 = load ptr, ptr %1, align 8
  %22 = getelementptr inbounds %struct.Node, ptr %21, i32 0, i32 1
  %23 = load atomic i64, ptr %22 acquire, align 8
  store i64 %23, ptr %6, align 8
  %24 = load ptr, ptr %6, align 8
  store ptr %24, ptr %2, align 8
  %25 = load ptr, ptr %1, align 8
  %26 = load atomic i64, ptr @Head acquire, align 8
  store i64 %26, ptr %7, align 8
  %27 = load ptr, ptr %7, align 8
  %28 = icmp eq ptr %25, %27
  br i1 %28, label %29, label %74

29:                                               ; preds = %20
  %30 = load ptr, ptr %2, align 8
  %31 = icmp eq ptr %30, null
  br i1 %31, label %32, label %33

32:                                               ; preds = %29
  store i32 -1, ptr %4, align 4
  br label %75

33:                                               ; preds = %29
  %34 = load ptr, ptr %2, align 8
  %35 = getelementptr inbounds %struct.Node, ptr %34, i32 0, i32 0
  %36 = load i32, ptr %35, align 8
  store i32 %36, ptr %4, align 4
  %37 = load ptr, ptr %2, align 8
  store ptr %37, ptr %8, align 8
  %38 = load i64, ptr %1, align 8
  %39 = load i64, ptr %8, align 8
  %40 = cmpxchg ptr @Head, i64 %38, i64 %39 acq_rel monotonic, align 8
  %41 = extractvalue { i64, i1 } %40, 0
  %42 = extractvalue { i64, i1 } %40, 1
  br i1 %42, label %44, label %43

43:                                               ; preds = %33
  store i64 %41, ptr %1, align 8
  br label %44

44:                                               ; preds = %43, %33
  %45 = zext i1 %42 to i8
  store i8 %45, ptr %9, align 1
  %46 = load i8, ptr %9, align 1
  %47 = trunc i8 %46 to i1
  br i1 %47, label %48, label %72

48:                                               ; preds = %44
  %49 = load atomic i64, ptr @Tail acquire, align 8
  store i64 %49, ptr %10, align 8
  %50 = load ptr, ptr %10, align 8
  store ptr %50, ptr %3, align 8
  %51 = load ptr, ptr %3, align 8
  %52 = icmp ne ptr %51, null
  br i1 %52, label %53, label %54

53:                                               ; preds = %48
  br label %55

54:                                               ; preds = %48
  call void @__assert_fail(ptr noundef @.str, ptr noundef @.str.1, i32 noundef 73, ptr noundef @__PRETTY_FUNCTION__.dequeue) #5
  unreachable

55:                                               ; preds = %53
  %56 = load ptr, ptr %1, align 8
  %57 = load ptr, ptr %3, align 8
  %58 = icmp eq ptr %56, %57
  br i1 %58, label %59, label %71

59:                                               ; preds = %55
  %60 = load ptr, ptr %2, align 8
  store ptr %60, ptr %11, align 8
  %61 = load i64, ptr %3, align 8
  %62 = load i64, ptr %11, align 8
  %63 = cmpxchg ptr @Tail, i64 %61, i64 %62 acq_rel monotonic, align 8
  %64 = extractvalue { i64, i1 } %63, 0
  %65 = extractvalue { i64, i1 } %63, 1
  br i1 %65, label %67, label %66

66:                                               ; preds = %59
  store i64 %64, ptr %3, align 8
  br label %67

67:                                               ; preds = %66, %59
  %68 = zext i1 %65 to i8
  store i8 %68, ptr %12, align 1
  %69 = load i8, ptr %12, align 1
  %70 = trunc i8 %69 to i1
  br label %71

71:                                               ; preds = %67, %55
  br label %75

72:                                               ; preds = %44
  br label %73

73:                                               ; preds = %72
  br label %74

74:                                               ; preds = %73, %20
  br label %13

75:                                               ; preds = %71, %32
  %76 = load i32, ptr %4, align 4
  ret i32 %76
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local ptr @worker(ptr noundef %0) #0 {
  %2 = alloca ptr, align 8
  %3 = alloca i64, align 8
  %4 = alloca i32, align 4
  store ptr %0, ptr %2, align 8
  %5 = load ptr, ptr %2, align 8
  %6 = ptrtoint ptr %5 to i64
  store i64 %6, ptr %3, align 8
  %7 = load i64, ptr %3, align 8
  %8 = trunc i64 %7 to i32
  call void @enqueue(i32 noundef %8)
  %9 = call i32 @dequeue()
  store i32 %9, ptr %4, align 4
  %10 = load i32, ptr %4, align 4
  %11 = icmp ne i32 %10, -1
  br i1 %11, label %12, label %13

12:                                               ; preds = %1
  br label %14

13:                                               ; preds = %1
  call void @__assert_fail(ptr noundef @.str.3, ptr noundef @.str.4, i32 noundef 16, ptr noundef @__PRETTY_FUNCTION__.worker) #5
  unreachable

14:                                               ; preds = %12
  ret ptr null
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca [3 x i64], align 16
  %3 = alloca i32, align 4
  store i32 0, ptr %1, align 4
  call void @init()
  store i32 0, ptr %3, align 4
  br label %4

4:                                                ; preds = %15, %0
  %5 = load i32, ptr %3, align 4
  %6 = icmp slt i32 %5, 3
  br i1 %6, label %7, label %18

7:                                                ; preds = %4
  %8 = load i32, ptr %3, align 4
  %9 = sext i32 %8 to i64
  %10 = getelementptr inbounds [3 x i64], ptr %2, i64 0, i64 %9
  %11 = load i32, ptr %3, align 4
  %12 = sext i32 %11 to i64
  %13 = inttoptr i64 %12 to ptr
  %14 = call i32 @pthread_create(ptr noundef %10, ptr noundef null, ptr noundef @worker, ptr noundef %13) #6
  br label %15

15:                                               ; preds = %7
  %16 = load i32, ptr %3, align 4
  %17 = add nsw i32 %16, 1
  store i32 %17, ptr %3, align 4
  br label %4, !llvm.loop !6

18:                                               ; preds = %4
  ret i32 0
}

; Function Attrs: nounwind
declare i32 @pthread_create(ptr noundef, ptr noundef, ptr noundef, ptr noundef) #3

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { nounwind allocsize(0) "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #2 = { noreturn nounwind "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #3 = { nounwind "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #4 = { nounwind allocsize(0) }
attributes #5 = { noreturn nounwind }
attributes #6 = { nounwind }

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
