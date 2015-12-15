//
//  Dot.m
//  Sizzle
//
//  Created by Thomas Suarez on 7/9/15.
//  Copyright (c) 2015 CarrotCorp. All rights reserved.
//

#import "Dot.h"

@implementation Dot {
    Game *game; // current game object
    int dotSize; // width & height of this Dot view
    int firstX; // before inertia
    int firstY; // before inertia
    bool touching; // is user currently touching this Dot?
    bool moved; // was the Dot moved by the user while touching?
    bool burning; // is this Dot currently burning up in the fire?
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (id)initWithFrame:(CGRect)frame dotType:(int)type {
    self = [super initWithFrame:frame];
    if (self) {
        // Set background clear/transparent
        [self setBackgroundColor:[[UIColor alloc] initWithWhite:0.0f alpha:0.0f]];
        self.alpha = 0.0; // set Dot invisible (until fade in)
        
        // Add Dot image
        dotSize = frame.size.width;
        CGRect dotFrame = CGRectMake(0, 0, dotSize, dotSize);
        UIImageView *dotView = [[UIImageView alloc] initWithFrame:dotFrame];
        NSString *imagePath = [NSString stringWithFormat:@"dot%dnorm.png", type+1];
        dotView.image = [UIImage imageNamed:imagePath];
        [self addSubview:dotView];
        
        // Fade in Dot
        [UIView animateWithDuration:0.25 animations:^{
            self.alpha = 1.0;
        }];
        
        // Setup intertial scrolling
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(moveDot:)];
        pan.minimumNumberOfTouches = 1;
        pan.maximumNumberOfTouches = 1;
        [self addGestureRecognizer:pan];
        
        // Start gravity on this Dot
        [self performSelectorInBackground:@selector(gravity) withObject:nil];
        
        // Start checking for fire
        [NSTimer scheduledTimerWithTimeInterval:0.1
            target:self
            selector:@selector(checkFire)
            userInfo:nil
            repeats:YES];
        }
    return self;
}

/*- (void)moveDot:(UIPanGestureRecognizer *)pan {
    self.center = [pan locationInView:self.superview];
}*/

- (void)setGame:(Game *)newGame {
    game = newGame;
}

- (void)checkFire {
    if (!burning && self.center.y > [Game fireLimit]) {
        burning = true;
        [self burn];
    }
}

- (void)burn {
    // Decrement score
    [game addScore:-10];
    
    // Crossfade white-to-red
    
    // Wait for crossfade
    
    // Vibrate device
    
    // Fade out completely
    [UIView animateWithDuration:0.25 animations:^{
        self.alpha = 0;
    }];
    
    // Wait for fadeout
    
    // Destroy this object
    [self removeFromSuperview];
}

- (void)gravity {
    dispatch_queue_t workQ = dispatch_queue_create("Dot", 0);
    
    dispatch_async(workQ, ^{
        
        int gameTime;
        
        // Get current coordinates
        float x = self.center.x;
        float y = self.center.y;
        
        // Gravity, while not being touched or burned up.
        while (!touching && !burning) {
            gameTime = [Game gameTime];
            y += (gameTime / 10) * 0.5; // add to y value
            
            // Update current Dot view center (on main thread)
            dispatch_sync(dispatch_get_main_queue(), ^{
                self.center = CGPointMake(x,y);
            });
            
            usleep(20);
        }
    });
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    touching = true;
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    moved = true;
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    // If the dot was only tapped, not moved, restart gravity (inertia was never called)
    if (!moved) {
        [self inertiaFinished];
    }
    moved = false;
}

- (void)inertiaFinished {
    touching = false;
    [self gravity]; // restart gravity
}

/* See http://stackoverflow.com/a/6687064 */
- (void)moveDot:(id)sender {
    [self bringSubviewToFront:[(UIPanGestureRecognizer*)sender view]];
    CGPoint translatedPoint = [(UIPanGestureRecognizer*)sender translationInView:self];
    
    if ([(UIPanGestureRecognizer*)sender state] == UIGestureRecognizerStateBegan) {
        firstX = [[sender view] center].x;
        firstY = [[sender view] center].y;
    }
    
    translatedPoint = CGPointMake(firstX+translatedPoint.x, firstY+translatedPoint.y);
    
    [[sender view] setCenter:translatedPoint];
    
    if ([(UIPanGestureRecognizer*)sender state] == UIGestureRecognizerStateEnded) {
        CGFloat velocityX = (0.03*[(UIPanGestureRecognizer*)sender velocityInView:self].x);
        CGFloat velocityY = (0.03*[(UIPanGestureRecognizer*)sender velocityInView:self].y);
        
        CGFloat finalX = translatedPoint.x + velocityX;
        CGFloat finalY = translatedPoint.y + velocityY;
        
        CGFloat animationDuration = (ABS(velocityY)*.0001)+.2;
        
        [UIView beginAnimations:nil context:NULL];
        [UIView setAnimationDuration:animationDuration];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
        [UIView setAnimationDelegate:self];
        [UIView setAnimationDidStopSelector:@selector(inertiaFinished)];
        [[sender view] setCenter:CGPointMake(finalX, finalY)];
        [UIView commitAnimations];
    }
}

@end
