# reddit-crawler

Crawl through reddit posts through users.

## Usage

No really just add to dependencies

```
[reddit-crawler "0.1.0"]
```

and then you can get a lazy seq of posts with

```
(take 100 (neighbour-posts "1rgowj" :order 2))
```

To get 100 similar posts that are 2 layers away from "1rgowj"

## Rationale

The aim of this project is to make posts in reddit.com more
accessible. The main idea is that each reddit post has, besides the
content, a bunch of metadata like the author, the subreddit, the
comments etc and in turn from those we can extract other metadata for
example from the post author we can extract other posts or the
subreddits she has subsribed to etc. From all this information reddit
uses only the subreddit, the likes/dislikes rating and the time of
post to expose a post to a specific user. This project attempts to use
other metadata to come up with more interesting and smart ways to
expose posts to interested users.

The way it works is, from the data collected about a post, we create a
weighted graph which connects the post to other posts. Using a best
first search we come up with other posts that are connected to a
specific post or user.

Most of the above functionality works for the simple case of finding
other posts through the original post's comment authors.

## License

GNU GPLv2
