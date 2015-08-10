//
//  LevelPicker.m
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import "LevelPicker.h"
#import "Game.h"
#import "Helper.h"

@interface LevelPicker () {
    IBOutlet UIImageView *fireView;
}

@end

@implementation LevelPicker {
    int currentLevel;
}
const int totalLevels = 6;

- (IBAction)click:(UIButton *)button {
    Game *game = [[Game alloc] initWithLevel:(int)button.tag maxLevel:currentLevel];
    [self presentViewController:game animated:YES completion:nil];
    game = nil;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Animate fire GIF view
    [Helper animateFire:fireView];
    
    // Get current level
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    currentLevel = (int)[defaults integerForKey:@"currentLevel"];
    
    // If necessary...
    if (currentLevel == 0) { // defaults value not set
        currentLevel = 1; // set initial value
        
        // Save to defaults
        [defaults setInteger:currentLevel forKey:@"currentLevel"];
        [defaults synchronize];
    }
    
    // Lock future levels
    UIColor *color = [[UIColor alloc] initWithWhite:0.3 alpha:1.0];
    for (int i = totalLevels; i > currentLevel; i--) {
        UIButton *button = (UIButton *)[self.view viewWithTag:i];
        [button setTintColor:color];
        [button setEnabled:false];
    }
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
