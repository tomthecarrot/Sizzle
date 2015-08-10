#include "gif.h"

void cleanUp(GifInfo *info) {
    info->surfaceDescriptor = NULL;
    free(info->backupPtr);
    info->backupPtr = NULL;
    free(info->controlBlock);
    info->controlBlock = NULL;
    free(info->rasterBits);
    info->rasterBits = NULL;
    free(info->comment);
    info->comment = NULL;

    GifFileType *GifFile = info->gifFilePtr;
    DGifCloseFile(GifFile);
    free(info);
}

jobject createGifHandle(GifSourceDescriptor *descriptor, JNIEnv *env, jboolean justDecodeMetaData) {
    if (descriptor->startPos < 0) {
        descriptor->Error = D_GIF_ERR_NOT_READABLE;
        DGifCloseFile(descriptor->GifFileIn);
    }
    if (descriptor->Error != 0 || descriptor->GifFileIn == NULL) {
        throwGifIOException(descriptor->Error, env);
        return NULL;
    }

    GifInfo *info = malloc(sizeof(GifInfo));
    if (info == NULL) {
        DGifCloseFile(descriptor->GifFileIn);
        throwException(env, OUT_OF_MEMORY_ERROR, OOME_MESSAGE);
        return NULL;
    }
    info->controlBlock = calloc(sizeof(GraphicsControlBlock), 1);
    if (info->controlBlock == NULL) {
        DGifCloseFile(descriptor->GifFileIn);
        throwException(env, OUT_OF_MEMORY_ERROR, OOME_MESSAGE);
        return NULL;
    }
    info->gifFilePtr = descriptor->GifFileIn;
    info->startPos = descriptor->startPos;
    info->currentIndex = 0;
    info->nextStartTime = 0;
    info->lastFrameRemainder = -1;
    info->comment = NULL;
    info->loopCount = 1;
    info->currentLoop = 0;
    info->speedFactor = 1.0;
    info->sourceLength = descriptor->sourceLength;

    info->backupPtr = NULL;
    info->rewindFunction = descriptor->rewindFunc;
    info->surfaceDescriptor = NULL;
    info->isOpaque = JNI_FALSE;

    DDGifSlurp(info, false);
    if (justDecodeMetaData == JNI_TRUE)
        info->rasterBits = NULL;
    else {
        info->rasterBits = malloc(
                descriptor->GifFileIn->SHeight * descriptor->GifFileIn->SWidth * sizeof(GifPixelType));
        if (info->rasterBits == NULL) {
            descriptor->GifFileIn->Error = D_GIF_ERR_NOT_ENOUGH_MEM;
        }
    }

    if (descriptor->GifFileIn->SWidth < 1 || descriptor->GifFileIn->SHeight < 1) {
        DGifCloseFile(descriptor->GifFileIn);
        throwGifIOException(D_GIF_ERR_INVALID_SCR_DIMS, env);
        return NULL;
    }
    if (descriptor->GifFileIn->Error == D_GIF_ERR_NOT_ENOUGH_MEM) {
        cleanUp(info);
        throwException(env, OUT_OF_MEMORY_ERROR, OOME_MESSAGE);
        return NULL;
    }
#if defined(STRICT_FORMAT_89A)
        descriptor->Error = descriptor->GifFileIn->Error;
#endif

    if (descriptor->GifFileIn->ImageCount == 0) {
        descriptor->Error = D_GIF_ERR_NO_FRAMES;
    }
    else if (descriptor->GifFileIn->Error == D_GIF_ERR_REWIND_FAILED) {
        descriptor->Error = D_GIF_ERR_REWIND_FAILED;
    }
    if (descriptor->Error != 0) {
        cleanUp(info);
        throwGifIOException(descriptor->Error, env);
        return NULL;
    }
    jclass gifInfoHandleClass = (*env)->FindClass(env, "pl/droidsonroids/gif/GifInfoHandle");
    if (gifInfoHandleClass == NULL) {
        cleanUp(info);
        return NULL;
    }
    jmethodID gifInfoHandleCtorMID = (*env)->GetMethodID(env, gifInfoHandleClass, "<init>", "(JIII)V");
    if (gifInfoHandleCtorMID == NULL) {
        cleanUp(info);
        return NULL;
    }
    return (*env)->NewObject(env, gifInfoHandleClass, gifInfoHandleCtorMID,
                             (jlong) (intptr_t) info, info->gifFilePtr->SWidth, info->gifFilePtr->SHeight,
                             info->gifFilePtr->ImageCount);
}