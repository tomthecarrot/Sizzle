//
//  GameOver.m
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import "GameOver.h"
#import "Helper.h"

@interface GameOver () {
    IBOutlet UIImageView *fireView;
}

@end

@implementation GameOver
int _score = 0;

- (id)initWithScore:(int)score {
    _score = score;
    
    return self;
}

- (IBAction)play:(id)sender {
    // Transfer to Vungle ad, then back to main menu
    UIViewController *parent = self.presentingViewController;
    [self dismissViewControllerAnimated:NO completion:^{
        // Play Vungle ad
        VungleSDK *sdk = [VungleSDK sharedSDK];
        [sdk playAd:parent error:nil];
    }];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Update score view
    scoreView.text = [NSString stringWithFormat:@"%d", _score];
    
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
