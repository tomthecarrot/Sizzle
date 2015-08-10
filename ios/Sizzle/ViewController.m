//
//  ViewController.m
//  Sizzle
//
//  Created by Thomas Suarez on 7/8/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import "ViewController.h"
#import "Tutorial.h"
#import "Game.h"
#import "LevelPicker.h"
#import "Helper.h"
#import <AVFoundation/AVFoundation.h>

@interface ViewController () {
    IBOutlet UIImageView *fireView;
}

@end

@implementation ViewController
AVAudioPlayer *music;

- (IBAction)play:(id)sender {
    LevelPicker *game = [[LevelPicker alloc] init];
    [self presentViewController:game animated:YES completion:nil];
    game = nil;
}

- (IBAction)tutorial:(id)sender {
    Tutorial *tut = [[Tutorial alloc] init];
    [self presentViewController:tut animated:YES completion:nil];
    tut = nil;
}

- (IBAction)opensource:(id)sender {
    // Get raw txt file
    NSString *path = [[NSBundle mainBundle] pathForResource:@"opensource-ios" ofType:@"txt"];
    NSString *txt = [[NSString alloc] initWithContentsOfFile:path usedEncoding:nil error:nil];
    
    // Show alert
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Open Source" message:txt
                                                   delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
    [alert show];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Start animating fire GIF view
    [Helper animateFire:fireView];
    
    // Start music
    NSURL *url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:@"bgmusic" ofType:@"mp3"]];
    music = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
    music.numberOfLoops = -1; // forever looping
    //[music play];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
