let vue = new Vue( {
    el: '#app',

    data() {
        return {
            backgrounds: [
                {
                    image: 'background-1.png',
                    animationSpeed: 0.4,
                },
                {
                    image: 'background-2.png',
                    animationSpeed: 0.35,
                },
                {
                    image: 'background-3.png',
                    animationSpeed: 0.15,
                },
                {
                    image: 'background-4.png',
                    animationSpeed: 0.1,
                }
            ],
            scroll: 0,
            maxScroll: 2000
        }
    },

    methods: {
        loadBackgrounds() {
            let parrent = document.getElementById('backgroundParent')
            for (index in this.backgrounds) {
                let background = this.backgrounds[index]
                let image = document.createElement('img')

                image.src = 'assets/' + background.image
                image.id = background.image
                image.classList.add("backgroundImage") 
                parrent.appendChild(image)
            }
        },
        onScroll(event) {
            let scrollAmount = event.srcElement.scrollingElement.scrollTop
            
            for (index in this.backgrounds) {
                let background = this.backgrounds[index]
                let image = document.getElementById(background.image)

                let speed = background.animationSpeed 
                
                image.style.top = scrollAmount * speed + 'px'
                // set image position
            }
            this.scroll += scrollAmount 
        },
        redirect(url, newTab) {
            if (newTab) {
                window.open(url, '_blank')
            } else {
                window.open(url, '_self')
            }
        },
        download() {
            let url = './assets/LethalHabit.jar'

            const link = document.createElement('a')
            link.href = url
            link.download = url.split('/').pop()
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
        },
        isPhone() {
        return (navigator.userAgent.match(/Android/i)
            || navigator.userAgent.match(/webOS/i)
            || navigator.userAgent.match(/iPhone/i)
            || navigator.userAgent.match(/iPad/i)
            || navigator.userAgent.match(/iPod/i)
            || navigator.userAgent.match(/BlackBerry/i)
            || navigator.userAgent.match(/Windows Phone/i))
        }
        
    },
    created() {
        this.loadBackgrounds()
        if (!this.isPhone()) {
            window.onscroll = this.onScroll
        }
        window.onbeforeunload = function () {
            window.scrollTo(0, 0);
        }
    },
})