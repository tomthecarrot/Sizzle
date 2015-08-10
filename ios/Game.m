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

@implementation Game
static const int dotSize = 100;
int currentLevel = 1; // this level
int maxLevel = 1; // max level user has ever reached
int score = 0; // current score
int gameTime = 0; // amount of 100ms intervals this game has been running for
int fireLimit = 0; // minimum Y coordinate value for fire burn-up
int maxDots = 0; // maximum amount of dots for this game. -1 = unlimited.
int numDotsTotal = 0; // number of dots ever on screen (during this game)
int totalTime = 0; // amount of 100ms intervals in total game time
int maxX = 0;
int maxY = 0;

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
    maxDots = [[levelData objectAtIndex:0] intValue];
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
    //[self dismissViewControllerAnimated:YES completion:nil];
    
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
    GameOver *gameover = [[GameOver alloc] initWithScore:score];
    [self presentViewController:gameover animated:YES completion:nil];
}

- (void)addTime {
    gameTime++;
    
    bool isMultipleOfTen = !(gameTime % 10); // every second (10x100ms)
    if (isMultipleOfTen && numDotsTotal < maxDots && gameTime < totalTime) {
        [self spawnDot:EMOTICON];
    }
    
    if (gameTime >= totalTime || score <= 0) {
        [self endGame];
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
    [NSTimer scheduledTimerWithTimeInterval:0.1
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
