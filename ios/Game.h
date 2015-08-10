//
//  Game.h
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface Game : UIViewController

- (id)initWithLevel:(int)_level maxLevel:(int)_maxLevel;
- (void)addScore:(int)add;
+ (int)gameTime;
+ (int)fireLimit;

@end
