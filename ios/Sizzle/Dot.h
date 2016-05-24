//
//  Dot.h
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Game.h"

@interface Dot : UIView {
    
}

- (void)setGame:(Game *)newGame;

- (id)initWithFrame:(CGRect)frame dotType:(int)type;

@end
