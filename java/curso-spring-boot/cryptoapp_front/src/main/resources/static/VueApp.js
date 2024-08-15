const {createApp} = Vue

const baseUrl = 'http://localhost:8080/coin'

const mainContainer = {
    data() {
        return {
            coins: [],
            formCoin: {
                isNew: true,
                name: '',
                price: '',
                quantity: '',
                title: 'Cadastrar nova transação',
                button: 'Cadastrar'
            }
        }
    },
    mounted() {
        this.showAllCoins()
    },
    methods: {
        showAllCoins() {
            axios.get(baseUrl)
                .then(res => res.data.map((item) => this.coins.push(item)))
                .catch(err => console.log(err))
        }
    }
}

createApp(mainContainer).mount('#app')