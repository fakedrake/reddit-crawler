# reddit-crawler

Crawl through reddit posts through users.

## Usage

Use responsibly...

No really just add to dependencies

```
[reddit-crawler "0.1.0"]
```

and then you can get a lazy seq of posts with

```
(take 100 (neighbour-posts "1rgowj" :order 2))
```

To get 100 similar posts that are 2 layers away from "1rgowj"

## License

GNU GPLv2
