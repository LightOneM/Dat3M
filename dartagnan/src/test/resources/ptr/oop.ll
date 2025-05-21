; ModuleID = '/home/lightone/Desktop/Dat3M/benchmarks/ptr/oop.c'
source_filename = "/home/lightone/Desktop/Dat3M/benchmarks/ptr/oop.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-i128:128-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

%struct.Data = type { i32 }

@int_array = dso_local global [5 x i32] zeroinitializer, align 16
@struct_array = dso_local global [5 x %struct.Data] zeroinitializer, align 16
@__const.main.arr = private unnamed_addr constant [3 x i32] [i32 10, i32 20, i32 30], align 4

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca [3 x i32], align 4
  %5 = alloca ptr, align 8
  store i32 0, ptr %1, align 4
  %6 = load i32, ptr getelementptr inbounds ([5 x i32], ptr @int_array, i64 0, i64 3), align 4
  store i32 %6, ptr %2, align 4
  %7 = load i32, ptr getelementptr inbounds ([5 x %struct.Data], ptr @struct_array, i64 0, i64 3), align 4
  store i32 %7, ptr %3, align 4
  call void @llvm.memcpy.p0.p0.i64(ptr align 4 %4, ptr align 4 @__const.main.arr, i64 12, i1 false)
  %8 = getelementptr inbounds [3 x i32], ptr %4, i64 0, i64 0
  %9 = getelementptr inbounds i32, ptr %8, i64 3
  store ptr %9, ptr %5, align 8
  ret i32 0
}

; Function Attrs: nocallback nofree nounwind willreturn memory(argmem: readwrite)
declare void @llvm.memcpy.p0.p0.i64(ptr noalias nocapture writeonly, ptr noalias nocapture readonly, i64, i1 immarg) #1

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { nocallback nofree nounwind willreturn memory(argmem: readwrite) }

!llvm.module.flags = !{!0, !1, !2, !3, !4}
!llvm.ident = !{!5}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{i32 8, !"PIC Level", i32 2}
!2 = !{i32 7, !"PIE Level", i32 2}
!3 = !{i32 7, !"uwtable", i32 2}
!4 = !{i32 7, !"frame-pointer", i32 2}
!5 = !{!"Ubuntu clang version 18.1.3 (1ubuntu1)"}
