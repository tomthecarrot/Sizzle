//
//  GameOver.h
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <VungleSDK/VungleSDK.h>

@interface GameOver : UIViewController {
    IBOutlet UILabel *scoreView;
}

- (id)initWithScore:(int)score;

@end
