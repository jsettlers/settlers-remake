################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../jni/NativeAreaWindow.c \
../jni/NativeGlWrapper.c 

OBJS += \
./jni/NativeAreaWindow.o \
./jni/NativeGlWrapper.o 

C_DEPS += \
./jni/NativeAreaWindow.d \
./jni/NativeGlWrapper.d 


# Each subdirectory must supply rules for building sources it contributes
jni/%.o: ../jni/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/lib/jvm/java-7-openjdk-amd64/include -O3 -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


