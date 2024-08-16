const {createApp} = Vue

const baseUrl = 'http://localhost:8080/coin'

const mainContainer = {
    data() {
        return {
            coins: [],
            canSeeTransactions: false,
            formCoin: {
                isNew: true,
                name: '',
                price: '',
                quantity: '',
                title: 'Cadastrar nova transação',
                button: 'Cadastrar'
            },
            transactions: []
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
        },
        showTransactions(name) {
            this.transactions = {
                coinName: name,
                data: []
            }

            this.canSeeTransactions = true

            axios.get(`${baseUrl}/${name}`)
                .then(res => {
                    console.log(res)
                    res.data.map(item => {
                        this.transactions.data.push({
                            id: item.id,
                            name: item.name,
                            price: item.price.toLocaleString('pt-br', {
                                style: 'currency',
                                currency: 'BRL'
                            }),
                            quantity: item.quantity,
                            dateTime: this.formattedDate(item.dateTime)
                        })
                    })
                })
                .catch(err => toastr.error(err, this.toastrOptions()))
        },
        saveCoin() {
            this.formCoin.name = this.formCoin.name.toUpperCase()
            this.formCoin.price = this.formCoin.price.replace('R$', '')
                .replace(',', '.').trim()

            if (this.formCoin.name === '' || this.formCoin.price === '' || this.formCoin.quantity === '') {
                toastr.error('Todos os campos são obrigatórios', 'Formulário', this.toastrOptions())
                return
            }

            const coin = {
                name: this.formCoin.name,
                price: this.formCoin.price,
                quantity: this.formCoin.quantity
            }

            const self = this

            axios.post(baseUrl, coin)
                .then(res => {
                    this.coins = []
                    toastr.success('Nova transação cadastrada com sucesso!', 'Formulário', this.toastrOptions())
                })
                .catch(err => toastr.error('Não foi possivel cadastrar uma nova transação.', 'Formulário', this.toastrOptions()))
                .then(function () {
                    self.showAllCoins()
                    self.showTransactions(coin.name)
                    self.cleanForm()
                })
        },
        cleanForm() {
            this.formCoin.isNew = true,
                this.formCoin.name = '',
                this.formCoin.price = '',
                this.formCoin.quantity = '',
                this.formCoin.title = 'Cadastrar nova transação',
                this.formCoin.button = 'Cadastrar'
        },
        formattedDate(date) {
            return (new Date(date.split('T')[0])).toLocaleDateString("pt-br")
        },
        toastrOptions() {
            return {
                timeOut: 5000,
                positionClass: 'toast-top-left'
            }
        }
    }
}

createApp(mainContainer).mount('#app')