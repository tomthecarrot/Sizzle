//
//  Helper.m
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import "Helper.h"
#import "UIImage+animatedGIF.h"

@implementation Helper

static AVAudioPlayer *music;

+ (void)animateFire:(UIImageView *)fireView {
    // Animate fire GIF view
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"fire" ofType:@"gif"];
    NSData *data = [NSData dataWithContentsOfFile:filePath];
    UIImage *fireGif = [UIImage animatedImageWithAnimatedGIFData:data];
    fireView.image = fireGif;
}

+ (void)startMusic {
    NSURL *url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:@"bgmusic" ofType:@"mp3"]];
    music = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
    music.numberOfLoops = -1; // forever looping
    [music play];
}

+ (void)playMusic {
    if (!music.isPlaying) {
        [music play];
    }
}

+ (void)pauseMusic {
    [music pause];
}

@end
