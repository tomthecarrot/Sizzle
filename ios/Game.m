//
//  Game.m
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import "Game.h"
#import "Helper.h"
#import "Dot.h"
#import "GameOver.h"

@interface Game () {
    IBOutlet UIImageView *fireView;
    IBOutlet UILabel *scoreView;
}

@end

@implementation Game {
    NSTimer *timer;
    int currentLevel; // this level
    int maxLevel; // max level user has ever reached
    int score; // current score
    int maxDots; // maximum amount of dots for this game. -1 = unlimited.
    int numDotsTotal; // number of dots ever on screen (during this game)
    int totalTime; // amount of 100ms intervals in total game time
    int maxX; // maximum X value on-screen
    int maxY; // maximum Y values on-screen
    
}
static const int dotSize = 100;
static int gameTime; // amount of 100ms intervals this game has been running for
static int fireLimit; // minimum Y coordinate value for fire burn-up

typedef enum {
    NORMAL, EMOTICON
} DotType;

- (id)initWithLevel:(int)_level maxLevel:(int)_maxLevel {
    currentLevel = _level;
    maxLevel = _maxLevel;
    
    // Read text file line-by-line
    NSString *filePath = [[NSBundle mainBundle]
                          pathForResource:@"levels" ofType:@"txt"];
    NSString *fileContents = [NSString stringWithContentsOfFile:filePath
                              encoding:NSUTF8StringEncoding error:nil];
    NSArray *array = [fileContents componentsSeparatedByString:@"\n"];
    
    NSString *levelString = [array objectAtIndex:currentLevel-1];
    NSArray *levelData = [levelString componentsSeparatedByString:@"/"];
    
    // Initialize variables
    score = 0;
    gameTime = 0;
    maxDots = [[levelData objectAtIndex:0] intValue];
    numDotsTotal = 0;
    totalTime = [[levelData objectAtIndex:1] intValue] * 10;
    CGRect screenBounds = [[UIScreen mainScreen] bounds];
    maxX = screenBounds.size.width;
    maxY = screenBounds.size.height / 3;
    fireLimit = maxY * 2; // 2/3 down
    
    return self;
}

- (void)addScore:(int)add {
    score += add;
    
    // Set label text
    NSString *text = [NSString stringWithFormat:@"%d", score];
    [scoreView setText:text];
}

- (void)spawnDot:(DotType)type {
    // Spawn Dot
    
    numDotsTotal++;
    
    int x = arc4random()%200; // random X coordinate
    int y = arc4random()%300; // random Y coordinate
    
    CGRect frame = CGRectMake(x, y, dotSize, dotSize);
    Dot *dot = [[Dot alloc] initWithFrame:frame dotType:type];
    dot.game = self;
    [self.view addSubview:dot];
}

- (void)endGame {
    // Next level if earned...
    if (score > 0) {
        int newLevel = maxLevel;
        if (currentLevel == maxLevel) { // proceed to next level...
            newLevel++;
        }
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults setInteger:newLevel forKey:@"currentLevel"];
        [defaults synchronize];
    }
    
    // Transfer to GameOver
    UIViewController *parent = self.presentingViewController;
    GameOver *gameover = [[GameOver alloc] initWithScore:score];
    [self dismissViewControllerAnimated:NO completion:^{
        [parent presentViewController:gameover animated:YES completion:nil];
    }];
}

- (void)addTime {
    gameTime++;
    
    bool isMultipleOfTen = !(gameTime % 10); // every second (10x100ms)
    if (isMultipleOfTen && numDotsTotal < maxDots && gameTime < totalTime) {
        [self spawnDot:EMOTICON];
    }
    
    if (gameTime >= totalTime || score <= 0) {
        [self endGame];
        [timer invalidate];
    }
}

+ (int)gameTime {
    return gameTime;
}

+ (int)fireLimit {
    return fireLimit;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Start game timer
    timer = [NSTimer scheduledTimerWithTimeInterval:0.1
                                     target:self
                                   selector:@selector(addTime)
                                   userInfo:nil
                                    repeats:YES];
    
    // Set score
    [self addScore:maxDots*10]; // start with 10 pts per Dot
    
    // Animate fire GIF view
    [Helper animateFire:fireView];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
